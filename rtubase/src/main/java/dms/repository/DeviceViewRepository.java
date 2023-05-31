package dms.repository;

import dms.entity.DeviceEntity;
import dms.entity.DeviceViewMainEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;

public interface DeviceViewRepository extends JpaRepository<DeviceViewMainEntity, Long>,
        JpaSpecificationExecutor<DeviceViewMainEntity> {

    @NonNull
    Page<DeviceViewMainEntity> findAll(Specification specification, @NonNull Pageable pageable);
}
