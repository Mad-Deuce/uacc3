package dms.service.sdev;

import dms.entity.standing.data.SDevEntity;

public interface SDevService {
    public SDevEntity findSDevByID(Long id);
}
