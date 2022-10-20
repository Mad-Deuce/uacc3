package dms.service.devobj;

import dms.entity.DevObjEntity;
import dms.repository.DevObjRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DevObjServiceImpl implements DevObjService{

    private final DevObjRepository devObjRepository;

    @Autowired
    public DevObjServiceImpl(DevObjRepository devObjRepository) {
        this.devObjRepository = devObjRepository;
    }

    @Override
    public Optional<DevObjEntity> findDevObjById(Long id) {
        return devObjRepository.findById(id);
    }


}
