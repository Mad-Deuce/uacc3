package dms.standing.data.service.device.type;

import dms.standing.data.entity.DeviceTypeEntity;
import dms.standing.data.repository.DeviceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceTypeServiceImpl implements DeviceTypeService {

    private final DeviceTypeRepository deviceTypeRepository;

    @Autowired
    public DeviceTypeServiceImpl(DeviceTypeRepository deviceTypeRepository) {
        this.deviceTypeRepository = deviceTypeRepository;
    }

    @Override
    public Optional<DeviceTypeEntity> findSDevByID(Long id) {
        if (id==null) return Optional.empty();
        return deviceTypeRepository.findById(id);
    }

    @Override
    public List<DeviceTypeEntity> findAllTypes() {
        return deviceTypeRepository.findAll();
    }
}
