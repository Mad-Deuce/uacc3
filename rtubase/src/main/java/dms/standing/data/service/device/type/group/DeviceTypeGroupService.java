package dms.standing.data.service.device.type.group;


import dms.standing.data.entity.DeviceTypeGroupEntity;

import java.util.Optional;

public interface DeviceTypeGroupService {
    Optional<DeviceTypeGroupEntity> findGroupByID(Integer id);
}
