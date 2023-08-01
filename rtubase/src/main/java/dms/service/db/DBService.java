package dms.service.db;

import java.time.LocalDate;
import java.util.List;

public interface DBService {

    void receivePDFiles() throws Exception;

    List<LocalDate> getDatesOfExistingSchemas();
}
