package dms.service.dev;

import dms.dto.DevDTO;
import dms.entity.DevEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface DevService {

    public DevEntity findDevById(Long id);

    public Page<DevEntity> findDevsBySpecification(Pageable pageable, DevDTO devDTO);

    public void deleteDevById(Long id);

    public void updateDev(DevEntity devModel);

    public DevEntity createDev(DevEntity devModel);
}
