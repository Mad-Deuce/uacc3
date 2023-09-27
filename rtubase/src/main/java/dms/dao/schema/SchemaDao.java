package dms.dao.schema;

import java.util.List;

public interface SchemaDao {

    String DRTU_SCHEMA_NAME = "drtu";

    List<String> getSchemaNameListLikeString(String likeString);

    boolean isSchemaExists(String schemaName);

    void renameSchema(String oldName, String newName);

    void removeSchema(String schemaName);

    void createSchema(String schemaName);

    void cloneSchema(String sourceSchemaName, String targetSchemaName);
}
