package dms.dao;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

@Component
public class SchemaManager {

    @PersistenceContext
    private  EntityManager em;

    @Transactional
    public void createSchema(String schemaName) {
        em.createNativeQuery(
                        "CREATE SCHEMA IF NOT EXISTS " +
                                schemaName +
                                " AUTHORIZATION postgres")
                .executeUpdate();
    }

    @Transactional
    public void renameSchema(String oldName, String newName) {
        em.createNativeQuery(
                        "ALTER SCHEMA " +
                                oldName +
                                " RENAME TO " +
                                newName)
                .executeUpdate();
    }

    @Transactional
    public void removeSchema(String schemaName) {
        em.createNativeQuery(
                        "DROP SCHEMA " +
                                schemaName +
                                " CASCADE")
                .executeUpdate();
    }
}
