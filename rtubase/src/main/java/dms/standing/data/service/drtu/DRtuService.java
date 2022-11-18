package dms.standing.data.service.drtu;

import dms.standing.data.entity.RtuObjectEntity;

import java.util.List;
import java.util.Optional;

public interface DRtuService {
    List<RtuObjectEntity> getAll();

   Optional<RtuObjectEntity> findById(String id);
}
