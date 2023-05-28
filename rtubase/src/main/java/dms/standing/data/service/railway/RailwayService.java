package dms.standing.data.service.railway;

import dms.standing.data.entity.RailwayEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface RailwayService {
     List<RailwayEntity> getAll();

     void importFromExcel(MultipartFile files) throws IOException;
}
