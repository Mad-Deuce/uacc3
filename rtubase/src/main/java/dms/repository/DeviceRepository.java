package dms.repository;

import dms.entity.DeviceEntity;
import dms.entity.LocationEntity;
import dms.standing.data.dock.val.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface DeviceRepository extends JpaRepository<DeviceEntity, Long>, JpaSpecificationExecutor {


    Page<DeviceEntity> findAll(Specification specification, Pageable pageable);

    Page<DeviceEntity> findAllByStatus(Status status, Pageable pageable);

    List<DeviceEntity> findAllByStatus(Status status);

    List<DeviceEntity> findAllById(Long id);

    List<DeviceEntity> findAllByLocation(LocationEntity location);


    @Query("SELECT count(d) " +
            "FROM DeviceEntity d " +
            "WHERE (d.status = '11' or d.status = '21' or d.status = '32') " +
            "and d.nextTestDate > :checkDate " +
            "and d.facility.id like :facilityIdPattern ")
    Long getNormalDeviceQuantity(@Param("checkDate") Date checkDate,
                                 @Param("facilityIdPattern") String facilityIdPattern);

    @Query("SELECT count(d) " +
            "FROM DeviceEntity d " +
            "WHERE (d.status = '2' or d.status = '12' or d.status = '23') " +
            "and d.facility.id like :facilityIdPattern ")
    Long getPassiveDeviceQuantity(@Param("facilityIdPattern") String facilityIdPattern);

    @Query("SELECT count(d) " +
            "FROM DeviceEntity d " +
            "WHERE (d.status = '11' or d.status = '21' or d.status = '32') " +
            "and d.nextTestDate <= :checkDate and d.extraNextTestDate > :checkDate " +
            "and d.facility.id like :facilityIdPattern ")
    Long getOverdueDeviceQuantity(@Param("checkDate") Date checkDate,
                                  @Param("facilityIdPattern") String facilityIdPattern);

    @Query("SELECT count(d) " +
            "FROM DeviceEntity d " +
            "WHERE (d.status = '11' or d.status = '21' or d.status = '32') " +
            "and d.extraNextTestDate <= :checkDate " +
            "and d.facility.id like :facilityIdPattern ")
    Long getExtraOverdueDeviceQuantity(@Param("checkDate") Date checkDate,
                                       @Param("facilityIdPattern") String facilityIdPattern);


}

