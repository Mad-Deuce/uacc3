package dms.dao;


import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class SchemaManager {

    @PersistenceContext
    private EntityManager em;

    public final String DRTU_SCHEMA_NAME = "drtu";
    public final String DOCK_SCHEMA_NAME = "dock";

    public void renameSchema(String oldName, String newName) {
        String queryString = String.format("ALTER SCHEMA %s RENAME TO %s", oldName, newName);
        em.createNativeQuery(queryString).executeUpdate();
    }

    public void removeSchema(String schemaName) {
        String queryString = String.format("DROP SCHEMA IF EXISTS %s CASCADE;", schemaName);
        em.createNativeQuery(queryString).executeUpdate();
    }

    public void restoreEmpty() {
        String command = "pg_restore -U postgres -w -d rtubase " +
                "/vagrant/ansible/roles/postgresql/files/d20230324.backup";

        Session session = null;
        ChannelExec channel = null;

        try {
            session = new JSch().getSession("postgres", "localhost", 2222);
            session.setPassword("postgres");
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channel.setInputStream(null);
            channel.setErrStream(System.err);

            channel.setOutputStream(responseStream);

            InputStream in = channel.getInputStream();
            channel.connect();

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    log.info(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    log.info("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception ignore) {
                }
            }

            while (channel.isConnected()) {
                Thread.sleep(100);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (session != null) {
                session.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    public void createDevicesMainView() {
        org.hibernate.Session session = em.unwrap(org.hibernate.Session.class);
        session.doWork(connection ->
                ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/CreateDeviceMainView.sql"))
        );
        session.close();
    }

    public String getSchemaNameListByDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("_yyyy_MM_dd");
        String dateString = date.format(formatter);
        List<String> schemaNameList = getSchemaNameList();
        return schemaNameList.stream().filter(value -> value.equals(DRTU_SCHEMA_NAME + dateString))
                .findFirst().orElse(null);
    }

    public List<String> getSchemaNameList() {
        return em.createNativeQuery(
                        "SELECT schema_name FROM information_schema.schemata WHERE schema_name LIKE :schemaName"
                ).setParameter("schemaName", DRTU_SCHEMA_NAME + "_%")
                .getResultList();
    }

    public List<String> getReceivedFileNameList(String schemaName) {
        String queryString = String.format("SELECT name FROM %s.dev_trans", schemaName);
        return em.createNativeQuery(queryString)
                .getResultList();
    }

    public void cloneSchema(String sourceSchemaName, String targetSchemaName) {
        String message = String.format("source schema %s does not exist!", sourceSchemaName);
        if (!isSchemaExists(sourceSchemaName)) throw new RuntimeException(message);
        message = String.format("target schema %s already exists!", targetSchemaName);
        if (isSchemaExists(targetSchemaName)) {
            removeSchema(targetSchemaName);
//            throw new RuntimeException(message);
        }
        createSchema(targetSchemaName);
        copySequences(sourceSchemaName, targetSchemaName);
        copyTables(sourceSchemaName, targetSchemaName, true);
        copyConstraints(sourceSchemaName, targetSchemaName);
        copyViews(sourceSchemaName, targetSchemaName);
//        copyFunctions(sourceSchemaName, targetSchemaName);
        createDevicesMainView();
    }

    public boolean isSchemaExists(String schemaName) {
        List<String> schemaNameList = em.createNativeQuery(
                        "SELECT schema_name FROM information_schema.schemata WHERE schema_name = :schemaName"
                ).setParameter("schemaName", schemaName)
                .getResultList();
        return !schemaNameList.isEmpty();
    }

    private void createSchema(String schemaName) {
        String queryString = String.format("CREATE SCHEMA %s AUTHORIZATION postgres", schemaName);
        em.createNativeQuery(queryString)
                .executeUpdate();
    }

    private void copySequences(String sourceSchemaName, String targetSchemaName) {
        List<Tuple> schemaNameList = em.createNativeQuery(
                        "SELECT * FROM information_schema.sequences WHERE sequence_schema = :sourceSchemaName",
                        Tuple.class)
                .setParameter("sourceSchemaName", sourceSchemaName)
                .getResultList();

        schemaNameList.forEach(value -> {
            String queryString = String.format("CREATE SEQUENCE IF NOT EXISTS %s INCREMENT BY %s MINVALUE %s " +
                            "MAXVALUE %s START WITH %s NO CYCLE ",
                    targetSchemaName + "." + value.get(2),
                    value.get(10),
                    value.get(8),
                    value.get(9),
                    value.get(7));
            em.createNativeQuery(queryString)
                    .executeUpdate();
        });

        log.info("fffff");
    }

    private void copyTables(String sourceSchemaName, String targetSchemaName, boolean copyData) {
        String queryString1 = String.format("SELECT CAST(TABLE_NAME AS text) FROM information_schema.tables " +
                        "WHERE table_schema = '%s' AND table_type = 'BASE TABLE'",
                sourceSchemaName);
        List<String> tableNameList = em.createNativeQuery(queryString1)
                .getResultList();
        tableNameList.forEach(tableName -> {
            String sourceTableName = sourceSchemaName + "." + tableName;
            String targetTableName = targetSchemaName + "." + tableName;
            String queryString = String.format("CREATE TABLE %s (LIKE %s INCLUDING ALL)",
                    targetTableName,
                    sourceTableName);
            em.createNativeQuery(queryString)
                    .executeUpdate();

            if (copyData) {
                queryString = String.format("INSERT INTO %s SELECT * FROM %s",
                        targetTableName,
                        sourceTableName);
                em.createNativeQuery(queryString)
                        .executeUpdate();
            }

            queryString = String.format("SELECT CAST(column_name AS text),  " +
                            "REPLACE(CAST(column_default AS text), '%s', '%s') " +
                            "FROM information_schema.COLUMNS " +
                            "WHERE table_schema = '%s' AND TABLE_NAME = '%s' " +
                            "AND column_default LIKE 'nextval(%%%s%%)'",
                    sourceSchemaName,
                    targetSchemaName,
                    targetSchemaName,
                    tableName,
                    sourceSchemaName);
            List<Tuple> tupleList = em.createNativeQuery(queryString, Tuple.class)
                    .getResultList();
            tupleList.forEach(tuple -> {
                em.createNativeQuery("ALTER TABLE :table ALTER COLUMN :column SET DEFAULT :default")
                        .setParameter("table", targetTableName)
                        .setParameter("column", tuple.get(0))
                        .setParameter("default", tuple.get(1))
                        .executeUpdate();
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
        String queryString1 = String.format(" SELECT CAST(TABLE_NAME AS text), view_definition " +
                        " FROM information_schema.views " +
                        " WHERE table_schema = '%s' ",
                sourceSchemaName);
        List<Tuple> viewList = em.createNativeQuery(queryString1, Tuple.class)
                .getResultList();
        viewList.forEach(tuple1 -> {
            String vName = targetSchemaName + "." + tuple1.get(0);
            String vDef = tuple1.get(1).toString();
            vDef = vDef.replaceAll(":", "\\\\:");
            String queryString = String.format("CREATE OR REPLACE VIEW %s AS %s",
                    vName, vDef);
            em.createNativeQuery(queryString)
                    .executeUpdate();

        });
    }

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
            func = func.replaceAll(sourceSchemaName, targetSchemaName);
            func = func.replaceAll(":", "\\\\:");
            em.createNativeQuery(func)
                    .executeUpdate();

        });


    }
}



