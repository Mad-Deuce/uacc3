package dms.service.schema;

import java.time.LocalDate;
import java.util.List;

public interface SchemaService {

    List<LocalDate> getDatesOfExistingSchemas();

    LocalDate getDateOfActiveSchema();

    LocalDate setActiveSchemaDate(LocalDate schemaDate);
}
