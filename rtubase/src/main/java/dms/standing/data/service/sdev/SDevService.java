package dms.standing.data.service.sdev;

import dms.standing.data.entity.DeviceTypeEntity;

import java.util.Optional;

public interface SDevService {
    Optional<DeviceTypeEntity> findSDevByID(Long id);
}
