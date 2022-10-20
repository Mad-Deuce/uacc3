package dms.service.devobj;

import dms.entity.DevObjEntity;

import java.util.Optional;

public interface DevObjService {

    public Optional<DevObjEntity> findDevObjById(Long id);
}
