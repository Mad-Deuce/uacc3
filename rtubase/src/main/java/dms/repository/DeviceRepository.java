package dms.repository;

import dms.entity.DeviceEntity;
import dms.entity.LocationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import javax.persistence.Tuple;
import java.sql.Date;
import java.util.List;

public interface DeviceRepository extends JpaRepository<DeviceEntity, Long>, JpaSpecificationExecutor<DeviceEntity> {

    @NonNull
    Page<DeviceEntity> findAll(Specification specification, @NonNull Pageable pageable);

    List<DeviceEntity> findAllByLocation(LocationEntity location);

    @Query("SELECT 'normal_devices', " +
            "rail.name, " +
            "rail.id, " +
            "sd.name, " +
            "sd.id, " +
            "d.status, " +
            "rtd.name, " +
            "rtd.id, " +
            "l.name, " +
            "l.id, " +
            "count (d)" +
            "FROM DeviceEntity  d " +
            "left join RailwayEntity rail on substring  (d.facility.id,1,1)=rail.id " +
            "left join SubdivisionEntity sd on substring  (d.facility.id,1,3)=sd.id " +
            "left join RtdFacilityEntity rtd on substring  (d.facility.id,1,4)=rtd.id " +
            "left join LineFacilityEntity l on substring  (d.facility.id,1,7)=l.id " +
            "WHERE (d.status = '11' or d.status = '21' or d.status = '32') " +
            "and d.nextTestDate > :checkDate " +
            "group by 1,2,3,4,5,6,7,8,9,10")
    List<Tuple> getNormalDevicesStatsAlt(@Param("checkDate") Date checkDate);

    @Query("SELECT 'overdue_devices', " +
            "rail.name, " +
            "rail.id, " +
            "sd.name, " +
            "sd.id, " +
            "d.status, " +
            "rtd.name, " +
            "rtd.id, " +
            "l.name, " +
            "l.id, " +
            "count (d)" +
            "FROM DeviceEntity  d " +
            "left join RailwayEntity rail on substring  (d.facility.id,1,1)=rail.id " +
            "left join SubdivisionEntity sd on substring  (d.facility.id,1,3)=sd.id " +
            "left join RtdFacilityEntity rtd on substring  (d.facility.id,1,4)=rtd.id " +
            "left join LineFacilityEntity l on substring  (d.facility.id,1,7)=l.id " +
            "WHERE (d.status = '11' or d.status = '21' or d.status = '32') " +
            "and d.nextTestDate <= :checkDate " +
            "and d.extraNextTestDate > :checkDate " +
            "group by 1,2,3,4,5,6,7,8,9,10")
    List<Tuple> getOverdueDevicesStatsAlt(@Param("checkDate") Date checkDate);

    @Query("SELECT 'extra_overdue_devices', " +
            "rail.name, " +
            "rail.id, " +
            "sd.name, " +
            "sd.id, " +
            "d.status, " +
            "rtd.name, " +
            "rtd.id, " +
            "l.name, " +
            "l.id, " +
            "count (d)" +
            "FROM DeviceEntity  d " +
            "left join RailwayEntity rail on substring  (d.facility.id,1,1)=rail.id " +
            "left join SubdivisionEntity sd on substring  (d.facility.id,1,3)=sd.id " +
            "left join RtdFacilityEntity rtd on substring  (d.facility.id,1,4)=rtd.id " +
            "left join LineFacilityEntity l on substring  (d.facility.id,1,7)=l.id " +
            "WHERE (d.status = '11' or d.status = '21' or d.status = '32') " +
            "and d.extraNextTestDate <= :checkDate " +
            "group by 1,2,3,4,5,6,7,8,9,10")
    List<Tuple> getExtraOverdueDevicesStatsAlt(@Param("checkDate") Date checkDate);

    @Query("SELECT 'passive_devices', " +
            "rail.name, " +
            "rail.id, " +
            "sd.name, " +
            "sd.id, " +
            "d.status, " +
            "rtd.name, " +
            "rtd.id, " +
            "l.name, " +
            "l.id, " +
            "count (d)" +
            "FROM DeviceEntity  d " +
            "left join RailwayEntity rail on substring  (d.facility.id,1,1)=rail.id " +
            "left join SubdivisionEntity sd on substring  (d.facility.id,1,3)=sd.id " +
            "left join RtdFacilityEntity rtd on substring  (d.facility.id,1,4)=rtd.id " +
            "left join LineFacilityEntity l on substring  (d.facility.id,1,7)=l.id " +
            "WHERE (d.status = '2' or d.status = '12' or d.status = '23') " +
            "group by 1,2,3,4,5,6,7,8,9,10")
    List<Tuple> getPassiveDevicesStatsAlt(@Param("checkDate") Date checkDate);
}

