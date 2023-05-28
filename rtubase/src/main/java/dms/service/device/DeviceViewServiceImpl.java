package dms.service.device;

import dms.entity.DeviceViewMainEntity;
import dms.filter.Filter;
import dms.repository.DeviceViewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceViewServiceImpl implements DeviceViewService {

    private final DeviceViewRepository deviceRepository;

    public DeviceViewServiceImpl(DeviceViewRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    public List<DeviceViewMainEntity> findAllDevices() {
        return deviceRepository.findAll();
    }

    @Override
    public Page<DeviceViewMainEntity> findDevicesBySpecification(Pageable pageable, List<Filter<Object>> filters) {
        return null;
    }
}
