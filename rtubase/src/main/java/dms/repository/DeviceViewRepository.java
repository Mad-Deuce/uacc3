package dms.repository;

import dms.entity.DeviceViewMainEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DeviceViewRepository extends JpaRepository<DeviceViewMainEntity, Long>,
        JpaSpecificationExecutor<DeviceViewMainEntity> {
}
