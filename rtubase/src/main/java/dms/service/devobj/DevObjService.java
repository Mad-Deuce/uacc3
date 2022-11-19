package dms.service.devobj;

import dms.entity.DeviceLocationEntity;

import java.util.Optional;

public interface DevObjService {

    Optional<DeviceLocationEntity> findDevObjById(Long id);
}
