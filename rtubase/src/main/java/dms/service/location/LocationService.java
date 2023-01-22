package dms.service.location;

import dms.entity.LocationEntity;

import java.util.Optional;

public interface LocationService {

    Optional<LocationEntity> findDevObjById(Long id);
}
