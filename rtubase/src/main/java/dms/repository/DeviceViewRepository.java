package dms.repository;

import dms.config.multitenant.TenantIdentifierResolver;
import dms.entity.DeviceEntity;
import dms.entity.DeviceViewMainEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;

import javax.transaction.Transactional;

public interface DeviceViewRepository extends JpaRepository<DeviceViewMainEntity, Long>,
        JpaSpecificationExecutor<DeviceViewMainEntity> {


//    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @NonNull
    Page<DeviceViewMainEntity> findAll(Specification specification, @NonNull Pageable pageable);
}
