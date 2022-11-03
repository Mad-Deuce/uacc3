package dms.service.dev;

import dms.entity.DeviceEntity;
import dms.filter.DevFilter;
import dms.property.name.constant.DevPropertyNameMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface DevService {

    public DeviceEntity findDevById(Long id);

    public Page<DeviceEntity> findDevsBySpecification(Pageable pageable, DevFilter devFilter);
    public Page<DeviceEntity> findDevsByQuery(Pageable pageable, DevFilter devFilter) throws NoSuchFieldException;

    public void deleteDevById(Long id);

    public void updateDev(Long id, DeviceEntity devModel, List<DevPropertyNameMapping> activeProperties);

    public DeviceEntity createDev(DeviceEntity devModel);
}
