package dms.standing.data.service.device.type;

import dms.standing.data.entity.DeviceTypeEntity;
import dms.standing.data.repository.SDevRepository;
import dms.standing.data.service.device.type.SDevService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SDevServiceImpl implements SDevService {

    private final SDevRepository sDevRepository;

    @Autowired
    public SDevServiceImpl(SDevRepository sDevRepository) {
        this.sDevRepository = sDevRepository;
    }

    @Override
    public Optional<DeviceTypeEntity> findSDevByID(Long id) {
        return sDevRepository.findById(id);
    }
}
