package dms.repository;

import dms.entity.DeviceLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DevObjRepository extends JpaRepository<DeviceLocationEntity, Long> {
}
