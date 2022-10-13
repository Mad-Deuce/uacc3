package dms.service.dev;

import dms.dto.DevDTO;
import dms.model.DevModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface DevService {

    public DevModel findDevById(Long id);

    public Page<DevModel> findDevsBySpecification(Pageable pageable, DevDTO devDTO);

    public void deleteDevById(Long id);

    public void updateDev(DevModel devModel);

    public DevModel createDev(DevModel devModel);
}
