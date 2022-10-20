package dms.standing.data.service.drtu;

import dms.standing.data.entity.DRtuEntity;

import java.util.List;
import java.util.Optional;

public interface DRtuService {
    public List<DRtuEntity> getAll();

   Optional<DRtuEntity> findById(String id);
}
