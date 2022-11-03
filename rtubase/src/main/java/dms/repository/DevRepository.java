package dms.repository;

import dms.standing.data.dock.val.Status;
import dms.entity.DeviceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


import java.util.List;

public interface DevRepository extends JpaRepository<DeviceEntity, Long>, JpaSpecificationExecutor {


    Page<DeviceEntity> findAll(Specification specification, Pageable pageable);

    Page<DeviceEntity> findAllByStatus(Status status, Pageable pageable);

    List<DeviceEntity> findAllByStatus(Status status);

    List<DeviceEntity> findAllById(Long id);
}
