package dms.service.device;

import dms.entity.DeviceEntity;
import dms.filter.DeviceFilter;
import dms.mapper.ExplicitDeviceMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface DeviceService {

    DeviceEntity findDeviceById(Long id);

    Page<DeviceEntity> findDevicesBySpecification(Pageable pageable, DeviceFilter deviceFilter);

    Page<DeviceEntity> findDevicesByQuery(Pageable pageable, DeviceFilter deviceFilter) throws NoSuchFieldException ;

    void deleteDeviceById(Long id);

    void updateDevice(Long id, DeviceEntity deviceEntity, List<ExplicitDeviceMatcher> activeProperties);

    DeviceEntity createDevice(DeviceEntity devModel);
}
