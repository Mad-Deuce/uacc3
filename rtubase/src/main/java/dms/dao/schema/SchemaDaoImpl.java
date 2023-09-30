package dms.dao.schema;


import dms.config.multitenant.DatabaseSessionManager;
import dms.config.multitenant.TenantIdentifierResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import java.util.List;


@Slf4j
@Component
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class SchemaDaoImpl implements SchemaDao {

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private TenantIdentifierResolver tenantIdentifierResolver;
    @Autowired
    private DatabaseSessionManager dsm;

    @Override
    public List<String> getSchemaNameListLikeString(String likeString) {
        String queryString = ("SELECT schema_name FROM information_schema.schemata WHERE schema_name LIKE '%s'");
        queryString = String.format(queryString, likeString);
        List<?> interimResult = em.createNativeQuery(queryString).getResultList();
        return interimResult.stream().map(Object::toString).toList();
    }

    @Override
    public boolean isSchemaExists(String schemaName) {
        String queryString = ("SELECT schema_name FROM information_schema.schemata WHERE schema_name = '%s'");
        queryString = String.format(queryString, schemaName);
        List<?> schemaNameList = em.createNativeQuery(queryString).getResultList();
        return !schemaNameList.isEmpty();
    }

    @Override
    public void renameSchema(String oldName, String newName) {
        String queryString = "ALTER SCHEMA %s RENAME TO %s";
        queryString = String.format(queryString, oldName, newName);
        em.createNativeQuery(queryString).executeUpdate();
    }

    @Override
    public void removeSchema(String schemaName) {
        String queryString = String.format("DROP SCHEMA IF EXISTS %s CASCADE;", schemaName);
        em.createNativeQuery(queryString).executeUpdate();
    }

    @Override
    public void createSchema(String schemaName) {
        String queryString = ("CREATE SCHEMA %s AUTHORIZATION postgres");
        queryString = String.format(queryString, schemaName);
        em.createNativeQuery(queryString).executeUpdate();
    }

    @Override
    public void cloneSchema(String sourceSchemaName, String targetSchemaName) {
        dsm.unbindSession();
        tenantIdentifierResolver.setCurrentTenant("public");
        dsm.bindSession();
        String message = String.format("source schema %s does not exist!", sourceSchemaName);
        if (!isSchemaExists(sourceSchemaName)) throw new RuntimeException(message);
        message = String.format("target schema %s already exists and will be removed!", targetSchemaName);
        if (isSchemaExists(targetSchemaName)) {
            log.info(message);
            removeSchema(targetSchemaName);
        }
        createSchema(targetSchemaName);
        copySequences(sourceSchemaName, targetSchemaName);
        copyTables(sourceSchemaName, targetSchemaName, true);
        copyConstraints(sourceSchemaName, targetSchemaName);
        copyViews(sourceSchemaName, targetSchemaName);
//        todo - must be finished
//        copyFunctions(sourceSchemaName, targetSchemaName);
    }


    private void copySequences(String sourceSchemaName, String targetSchemaName) {
        String queryString1 = "SELECT * FROM information_schema.sequences WHERE sequence_schema = '%s'";
        queryString1 = String.format(queryString1, sourceSchemaName);
        List<Tuple> schemaNameList = em.createNativeQuery(queryString1, Tuple.class).getResultList();

        schemaNameList.forEach(value -> {
            String queryString = "CREATE SEQUENCE IF NOT EXISTS %s INCREMENT BY %s MINVALUE %s " +
                    "MAXVALUE %s START WITH %s NO CYCLE ";
            queryString = String.format(queryString,
                    targetSchemaName + "." + value.get(2),
                    value.get(10),
                    value.get(8),
                    value.get(9),
                    value.get(7));
            em.createNativeQuery(queryString)
                    .executeUpdate();
        });
    }

    private void copyTables(String sourceSchemaName, String targetSchemaName, boolean copyData) {
        String queryString1 = "SELECT CAST(TABLE_NAME AS text) FROM information_schema.tables " +
                "WHERE table_schema = '%s' AND table_type = 'BASE TABLE'";
        queryString1 = String.format(queryString1, sourceSchemaName);
        List<String> tableNameList = em.createNativeQuery(queryString1).getResultList();

        tableNameList.forEach(tableName -> {
            String sourceTableName = sourceSchemaName + "." + tableName;
            String targetTableName = targetSchemaName + "." + tableName;
            String queryString2 = "CREATE TABLE %s (LIKE %s INCLUDING ALL)";
            queryString2 = String.format(queryString2, targetTableName, sourceTableName);
            em.createNativeQuery(queryString2).executeUpdate();

            if (copyData) {
                queryString2 = "INSERT INTO %s SELECT * FROM %s";
                queryString2 = String.format(queryString2, targetTableName, sourceTableName);
                em.createNativeQuery(queryString2).executeUpdate();
            }

            queryString2 = "SELECT CAST(column_name AS text), " +
                    "REPLACE(CAST(column_default AS text), '%s', '%s') " +
                    "FROM information_schema.COLUMNS " +
                    "WHERE table_schema = '%s' AND TABLE_NAME = '%s' " +
                    "AND column_default LIKE 'nextval(%%%s%%)'";
            queryString2 = String.format(queryString2,
                    sourceSchemaName, targetSchemaName,
                    targetSchemaName, tableName,
                    sourceSchemaName);
            List<Tuple> tupleList = em.createNativeQuery(queryString2, Tuple.class).getResultList();
            tupleList.forEach(tuple -> {
                String queryString3 = "ALTER TABLE %s ALTER COLUMN %s SET DEFAULT %s";
                queryString3 = String.format(queryString3, targetTableName, tuple.get(0), tuple.get(1));
                em.createNativeQuery(queryString3).executeUpdate();
            });

        });


    }

    private void copyConstraints(String sourceSchemaName, String targetSchemaName) {
        List<String> oidList = em.createNativeQuery(
                        "SELECT oid FROM pg_namespace WHERE nspname = :sourceSchemaName"
                )
                .setParameter("sourceSchemaName", sourceSchemaName)
                .getResultList();

        List<Tuple> constraintList = em.createNativeQuery(
                        "SELECT rn.relname, ct.conname, ct.oid " +
                                "FROM pg_constraint ct " +
                                "JOIN pg_class rn ON rn.oid = ct.conrelid " +
                                "WHERE " +
                                "CAST(ct.connamespace AS text) = CAST(:connamespace AS text) " +
                                "AND rn.relkind = 'r' AND ct.contype = 'f'"
                )
                .setParameter("connamespace", oidList.get(0))
                .getResultList();

        constraintList.forEach(tuple -> {
            String queryString = String.format("ALTER TABLE %s.%s ADD CONSTRAINT %s %s",
                    targetSchemaName, tuple.get(0),
                    tuple.get(1), tuple.get(2));
            em.createNativeQuery(queryString)
                    .executeUpdate();
        });
    }

    private void copyViews(String sourceSchemaName, String targetSchemaName) {
        String queryString1 = " SELECT TABLE_NAME, view_definition " +
                " FROM information_schema.views " +
                " WHERE table_schema = '%s' ";
        queryString1 = String.format(queryString1, sourceSchemaName);
        List<Tuple> viewList = em.createNativeQuery(queryString1, Tuple.class)
                .getResultList();
        viewList.forEach(tuple1 -> {
            String vName = targetSchemaName + "." + tuple1.get(0);
            String vDef = tuple1.get(1).toString();
            vDef = vDef.replaceAll(sourceSchemaName, targetSchemaName);
            vDef = vDef.replaceAll(":", "\\\\:");
            String queryString = String.format("CREATE OR REPLACE VIEW %s AS %s",
                    vName, vDef);
            em.createNativeQuery(queryString).executeUpdate();

        });
    }

    //        todo - must be finished
    private void copyFunctions(String sourceSchemaName, String targetSchemaName) {
        String srcOid = em.createNativeQuery(
                        "SELECT oid FROM pg_namespace WHERE nspname = :sourceSchemaName"
                )
                .setParameter("sourceSchemaName", sourceSchemaName)
                .getSingleResult().toString();

        List<String> oidList = em.createNativeQuery(
                        "SELECT CAST(oid AS text) " +
                                "FROM pg_proc " +
                                "WHERE CAST(pronamespace AS text) = CAST(:pronamespace AS text)"
                )
                .setParameter("pronamespace", srcOid)
                .getResultList();

        oidList.forEach(oid -> {
            String func = em.createNativeQuery(
                            "SELECT pg_get_functiondef(CAST(:func_oid AS oid))"
                    )
                    .setParameter("func_oid", oid)
                    .getSingleResult().toString();
            log.info("1------------ " + func);
            func = func.replaceAll(sourceSchemaName, targetSchemaName);
            log.info("2------------ " + func);
            func = func.replaceAll(":", "\\\\:");
            log.info("3------------ " + func);
            em.createNativeQuery(func)
                    .executeUpdate();

        });


    }
}



