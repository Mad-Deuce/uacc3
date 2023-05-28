package dms.service.device;


import dms.entity.DeviceViewMainEntity;
import dms.filter.Filter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface DeviceViewService {

    List<DeviceViewMainEntity> findAllDevices();

    Page<DeviceViewMainEntity> findDevicesBySpecification(Pageable pageable, List<Filter<Object>> filters);

}
