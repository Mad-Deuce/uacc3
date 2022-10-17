package dms.service.drail;

import dms.entity.standing.data.DRailEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DRailService {
    public List<DRailEntity> getAll();

    public void importFromExcel(MultipartFile files) throws IOException;
}
