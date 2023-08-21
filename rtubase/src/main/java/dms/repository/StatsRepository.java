package dms.repository;

import dms.entity.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.Tuple;
import javax.transaction.Transactional;
import java.sql.Date;
import java.util.List;


public interface StatsRepository extends JpaRepository<DeviceEntity, Long>, JpaSpecificationExecutor<DeviceEntity> {

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
            "WHERE (d.status = '11' or d.status = '21' or d.status = '32' or d.status = '51' or d.status = '52') " +
            "and d.nextTestDate > :checkDate " +
            "group by 1,2,3,4,5,6,7,8,9,10")
    List<Tuple> getNormalDevicesStats(@Param("checkDate") Date checkDate);

    @Query("SELECT 'normal_devices', " +
            "rail.name, " +
            "rail.id, " +
            "sd.name, " +
            "sd.id, " +
            "d.status, " +
            "count (d)" +
            "FROM DeviceEntity  d " +
            "left join RailwayEntity rail on substring  (d.facility.id,1,1)=rail.id " +
            "left join SubdivisionEntity sd on substring  (d.facility.id,1,3)=sd.id " +
            "WHERE (d.status = '11' or d.status = '21' or d.status = '32' or d.status = '51' or d.status = '52') " +
            "and d.nextTestDate > :checkDate " +
            "and d.facility.id LIKE :nodeId " +
            "group by 1,2,3,4,5,6")
    List<Tuple> getNormalDevicesStatsShort(@Param("checkDate") Date checkDate, @Param("nodeId") String nodeId);

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
            "WHERE (d.status = '11' or d.status = '21' or d.status = '32' or d.status = '51' or d.status = '52') " +
            "and d.nextTestDate <= :checkDate " +
            "and d.extraNextTestDate > :checkDate " +
            "group by 1,2,3,4,5,6,7,8,9,10")
    List<Tuple> getOverdueDevicesStats(@Param("checkDate") Date checkDate);

    @Query("SELECT 'overdue_devices', " +
            "rail.name, " +
            "rail.id, " +
            "sd.name, " +
            "sd.id, " +
            "d.status, " +
            "count (d)" +
            "FROM DeviceEntity  d " +
            "left join RailwayEntity rail on substring  (d.facility.id,1,1)=rail.id " +
            "left join SubdivisionEntity sd on substring  (d.facility.id,1,3)=sd.id " +
            "WHERE (d.status = '11' or d.status = '21' or d.status = '32' or d.status = '51' or d.status = '52') " +
            "and d.nextTestDate <= :checkDate " +
            "and d.extraNextTestDate > :checkDate " +
            "and d.facility.id LIKE :nodeId " +
            "group by 1,2,3,4,5,6")
    List<Tuple> getOverdueDevicesStatsShort(@Param("checkDate") Date checkDate, @Param("nodeId") String nodeId);

    @Transactional(Transactional.TxType.REQUIRES_NEW)
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
            "WHERE (d.status = '11' or d.status = '21' or d.status = '32' or d.status = '51' or d.status = '52') " +
            "and d.extraNextTestDate <= :checkDate " +
            "group by 1,2,3,4,5,6,7,8,9,10")
    List<Tuple> getExtraOverdueDevicesStats(@Param("checkDate") Date checkDate);

    @Query("SELECT 'extra_overdue_devices', " +
            "rail.name, " +
            "rail.id, " +
            "sd.name, " +
            "sd.id, " +
            "d.status, " +
            "count (d)" +
            "FROM DeviceEntity  d " +
            "left join RailwayEntity rail on substring  (d.facility.id,1,1)=rail.id " +
            "left join SubdivisionEntity sd on substring  (d.facility.id,1,3)=sd.id " +
            "WHERE (d.status = '11' or d.status = '21' or d.status = '32' or d.status = '51' or d.status = '52') " +
            "and d.extraNextTestDate <= :checkDate " +
            "and d.facility.id LIKE :nodeId " +
            "group by 1,2,3,4,5,6")
    List<Tuple> getExtraOverdueDevicesStatsShort(@Param("checkDate") Date checkDate, @Param("nodeId") String nodeId);

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
    List<Tuple> getPassiveDevicesStats(@Param("checkDate") Date checkDate);

    @Query("SELECT 'passive_devices', " +
            "rail.name, " +
            "rail.id, " +
            "sd.name, " +
            "sd.id, " +
            "d.status, " +
            "count (d)" +
            "FROM DeviceEntity  d " +
            "left join RailwayEntity rail on substring  (d.facility.id,1,1)=rail.id " +
            "left join SubdivisionEntity sd on substring  (d.facility.id,1,3)=sd.id " +
            "WHERE (d.status = '2' or d.status = '12' or d.status = '23') " +
            "and d.facility.id LIKE :nodeId " +
            "group by 1,2,3,4,5,6")
    List<Tuple> getPassiveDevicesStatsShort(@Param("nodeId") String nodeId);

    @Query(
            "SELECT " +
                    "SUBSTRING(d.facility.id, 1, :i) , " +
                    "COUNT (d)" +
                    "FROM DeviceEntity  d " +
                    "WHERE (d.status = '11' or d.status = '21' or d.status = '32' or d.status = '51' or d.status = '52') " +
                    "AND d.nextTestDate > :checkDate " +
                    "GROUP BY 1"
    )
    List<Tuple> getNormalDevicesQuantity(@Param("checkDate") Date checkDate, @Param("i") int i);

    @Query(
            "SELECT " +
                    "SUBSTRING(d.facility.id, 1, :i) , " +
                    "COUNT (d)" +
                    "FROM DeviceEntity  d " +
                    "WHERE (d.status = '11' or d.status = '21' or d.status = '32' or d.status = '51' or d.status = '52') " +
                    "and d.nextTestDate <= :checkDate " +
                    "GROUP BY 1"
    )
    List<Tuple> getExpiredDevicesQuantity(@Param("checkDate") Date checkDate, @Param("i") int i);

    @Query(
            "SELECT " +
                    "SUBSTRING(d.facility.id, 1, :i) , " +
                    "COUNT (d)" +
                    "FROM DeviceEntity  d " +
                    "WHERE (d.status = '11' or d.status = '21' or d.status = '32' or d.status = '51' or d.status = '52') " +
                    "and d.extraNextTestDate <= :checkDate " +
                    "GROUP BY 1"
    )
    List<Tuple> getExpiredWarrantyDevicesQuantity(@Param("checkDate") Date checkDate, @Param("i") int i);

    @Query(
            "SELECT " +
                    "SUBSTRING(d.facility.id, 1, :i) , " +
                    "COUNT (d)" +
                    "FROM DeviceEntity  d " +
                    "WHERE (d.status = '2' or d.status = '12') " +
                    "GROUP BY 1"
    )
    List<Tuple> getHidedDevicesQuantity(@Param("i") int i);
}

