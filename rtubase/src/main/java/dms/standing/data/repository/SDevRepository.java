package dms.standing.data.repository;

import dms.standing.data.entity.DeviceTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SDevRepository extends JpaRepository<DeviceTypeEntity, Long> {
}
