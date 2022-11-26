package dms.standing.data.service.dobj;

import dms.standing.data.entity.LineFacilityEntity;

import java.util.List;
import java.util.Optional;

public interface DObjService {
    List<LineFacilityEntity> getAll();

    Optional<LineFacilityEntity> findById(String id);
}
