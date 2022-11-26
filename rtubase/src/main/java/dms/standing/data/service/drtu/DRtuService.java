package dms.standing.data.service.drtu;

import dms.standing.data.entity.RtuFacilityEntity;

import java.util.List;
import java.util.Optional;

public interface DRtuService {
    List<RtuFacilityEntity> getAll();

   Optional<RtuFacilityEntity> findById(String id);
}
