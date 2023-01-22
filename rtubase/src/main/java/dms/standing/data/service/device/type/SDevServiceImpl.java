package dms.standing.data.service.device.type;

import dms.standing.data.entity.DeviceTypeEntity;
import dms.standing.data.repository.DeviceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SDevServiceImpl implements SDevService {

    private final DeviceTypeRepository deviceTypeRepository;

    @Autowired
    public SDevServiceImpl(DeviceTypeRepository deviceTypeRepository) {
        this.deviceTypeRepository = deviceTypeRepository;
    }

    @Override
    public Optional<DeviceTypeEntity> findSDevByID(Long id) {
        return deviceTypeRepository.findById(id);
    }
}
