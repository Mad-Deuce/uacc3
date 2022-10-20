package dms.repository;

import dms.entity.DevObjEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DevObjRepository extends JpaRepository<DevObjEntity, Long> {
}
