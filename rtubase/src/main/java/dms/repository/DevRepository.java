package dms.repository;

import dms.dock.val.Status;
import dms.entity.DevEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DevRepository extends JpaRepository<DevEntity, Long>, JpaSpecificationExecutor {

    Page<DevEntity> findAll(Specification specification, Pageable pageable);

    Page<DevEntity> findAllByStatus(Status status, Pageable pageable);

    List<DevEntity> findAllByStatus(Status status);

    List<DevEntity> findAllById(Long id);
}
