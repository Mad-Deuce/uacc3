package dms.standing.data.service.sdev;

import dms.standing.data.entity.SDevEntity;

import java.util.Optional;

public interface SDevService {
    public Optional<SDevEntity> findSDevByID(Long id);
}
