package dms.service.dev;

import dms.entity.DevEntity;
import dms.filter.DevFilter;
import dms.property.name.constant.DevPropertyNameConstant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;


public interface DevService {

    public DevEntity findDevById(Long id);

    public Page<DevEntity> findDevsBySpecification(Pageable pageable, DevFilter devFilter);

    public void deleteDevById(Long id);

    public void updateDev(Long id, DevEntity devModel, List<DevPropertyNameConstant> activeProperties);

    public DevEntity createDev(DevEntity devModel);
}
