package dms.standing.data.service.device.type;

import dms.standing.data.entity.DeviceTypeEntity;

import java.util.List;
import java.util.Optional;

public interface DeviceTypeService {
    Optional<DeviceTypeEntity> findSDevByID(Long id);

    List<DeviceTypeEntity> findAllTypes();
}
