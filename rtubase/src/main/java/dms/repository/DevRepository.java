package dms.repository;

import dms.model.DevModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DevRepository extends JpaRepository<DevModel, Long>, JpaSpecificationExecutor {

    Page<DevModel> findAll(Specification specification, Pageable pageable);

    Page<DevModel> findAllByPs(String ps, Pageable pageable);

    List<DevModel> findAllByPs(String ps);

    List<DevModel> findAllById(Long id);
}
