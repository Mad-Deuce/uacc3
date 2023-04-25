package dms.service.device;

import dms.entity.DeviceEntity;
import dms.filter.DeviceFilter;
import dms.filter.Filter;
import dms.mapper.ExplicitDeviceMatcher;
import dms.standing.data.dock.val.ReplacementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface DeviceService {

    DeviceEntity findDeviceById(Long id);

    Page<DeviceEntity> findDevicesByFilter(Pageable pageable, DeviceFilter deviceFilter) throws NoSuchFieldException;

    Page<DeviceEntity> findDevicesBySpecification(Pageable pageable, List<Filter<Object>> filters);

    void deleteDeviceById(DeviceEntity deviceEntity);

    void updateDevice(Long id, DeviceEntity deviceEntity, List<ExplicitDeviceMatcher> activeProperties);

    DeviceEntity createDevice(DeviceEntity devModel);

    void replaceDevice(DeviceEntity oldDeviceEntity, DeviceEntity newDeviceEntity, String status, ReplacementType replacementType);

    void setDeviceTo(DeviceEntity deviceEntity, String status, String facilityId, Long locationId);

    void unsetDevice(DeviceEntity deviceEntity, String facilityId);

    void decommissionDevice(DeviceEntity deviceEntity);
}
