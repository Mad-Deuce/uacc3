package dms.standing.data.service.facility;

import dms.standing.data.entity.LineFacilityEntity;

import java.util.List;
import java.util.Optional;

public interface LineFacilityService {
    List<LineFacilityEntity> getAll();

    Optional<LineFacilityEntity> findById(String id);
}
