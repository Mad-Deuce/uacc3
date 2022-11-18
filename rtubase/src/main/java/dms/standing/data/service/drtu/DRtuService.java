package dms.standing.data.service.drtu;

import dms.standing.data.entity.RtuEntity;

import java.util.List;
import java.util.Optional;

public interface DRtuService {
    public List<RtuEntity> getAll();

   Optional<RtuEntity> findById(String id);
}
