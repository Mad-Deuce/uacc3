package dms.service.device;

import dms.entity.DeviceEntity;
import dms.filter.DeviceFilter;
import dms.property.name.constant.DevicePropertyNameMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface DeviceService {

    DeviceEntity findDevById(Long id);

    Page<DeviceEntity> findDevsBySpecification(Pageable pageable, DeviceFilter deviceFilter);

    Page<DeviceEntity> findDevsByQuery(Pageable pageable, DeviceFilter deviceFilter) throws NoSuchFieldException;

    void deleteDevById(Long id);

    void updateDev(Long id, DeviceEntity devModel, List<DevicePropertyNameMapping> activeProperties);

    DeviceEntity createDev(DeviceEntity devModel);
}
