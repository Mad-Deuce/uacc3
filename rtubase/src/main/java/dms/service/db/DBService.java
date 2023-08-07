package dms.service.db;

import java.time.LocalDate;
import java.util.List;

public interface DBService {

    void receivePDFiles() throws Exception;

//    boolean isPDDirEmpty() throws Exception;

    List<LocalDate> getDatesOfExistingSchemas();

    LocalDate getDateOfActiveSchema();

    LocalDate setActiveSchemaDate(LocalDate schemaDate);
}
