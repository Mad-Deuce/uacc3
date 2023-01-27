package dms.standing.data.service.facility;

import dms.standing.data.entity.RtdFacilityEntity;

import java.util.List;
import java.util.Optional;

public interface RtdFacilityService {
    List<RtdFacilityEntity> getAll();

    Optional<RtdFacilityEntity> findById(String id);
}
