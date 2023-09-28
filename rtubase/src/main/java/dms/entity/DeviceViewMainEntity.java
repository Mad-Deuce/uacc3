package dms.entity;

import dms.standing.data.converter.LocateTypeConverter;
import dms.standing.data.converter.RegionTypeConverter;
import dms.standing.data.converter.StatusConverter;
import dms.standing.data.dock.val.LocateType;
import dms.standing.data.dock.val.RegionType;
import dms.standing.data.dock.val.Status;
import lombok.Data;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.sql.Date;

@Data
@Entity
@Table(name = "v$devices_main",  catalog = "rtubase")
public class DeviceViewMainEntity {

    @Id
    @Basic
    @Column(name = "id", nullable = false, columnDefinition = "NUMERIC(10,0)")
    private Long id;

    @Basic
    @Column(name = "type_id", nullable = false, columnDefinition = "NUMERIC")
    private Integer typeId;


    @Basic
    @Column(name = "type_name", nullable = false, length = 20)
    private String typeName;

    @Basic
    @Column(name = "type_group_id", nullable = false, columnDefinition = "NUMERIC")
    private Integer typeGroupId;

    @Basic
    @Column(name = "type_group_name", nullable = false, length = 160)
    private String typeGroupName;

    @Basic
    @Column(name = "number", nullable = false, length = 10)
    private String number;

    @Basic
    @Column(name = "release_year", nullable = false, length = 4)
    private String releaseYear;

    @Basic
    @Column(name = "test_date")
    private Date testDate;

    @Basic
    @Column(name = "next_test_date")
    private Date nextTestDate;



    @Formula(value = "case " +
            "when replacement_period * 0.1 > 3 " +
            "then (next_test_date + date_trunc('second', (3 || 'month')::interval))::date " +
            "else (next_test_date + date_trunc('second', ((replacement_period * 0.1)::numeric(5) || 'month')::interval))::date " +
            "end")
    private Date extraNextTestDate;


//    @Basic
//    @Column(name = "extra_next_test_date")
//    private Date extraNextTestDate;

    @Basic
    @Column(name = "replacement_period", nullable = false, columnDefinition = "NUMERIC(5,0)")
    private Integer replacementPeriod;

    @Convert(converter = StatusConverter.class)
    @Column(name = "status", nullable = false, length = 2, columnDefinition = "BPCHAR")
    private Status status;

    @Basic
    @Column(name = "detail", length = 160)
    private String detail;

    @Basic
    @Column(name = "railway_id", nullable = false, length = 1, columnDefinition = "BPCHAR")
    private String railwayId;

    @Basic
    @Column(name = "railway_name", nullable = false, length = 40)
    private String railwayName;

    @Basic
    @Column(name = "subdivision_id", nullable = false, length = 3)
    private String subdivisionId;

    @Basic
    @Column(name = "subdivision_short_name", nullable = false, length = 40)
    private String subdivisionShortName;

    @Basic
    @Column(name = "rtd_id", nullable = false, length = 4)
    private String rtdId;

    @Basic
    @Column(name = "rtd_name", nullable = false, length = 40)
    private String rtdName;

    @Basic
    @Column(name = "facility_id", length = 7)
    private String facilityId;

    @Basic
    @Column(name = "facility_name", length = 50)
    private String facilityName;

    @Basic
    @Column(name = "location_id", columnDefinition = "NUMERIC")
    private Long locationId;

    @Basic
    @Column(name = "label", length = 50)
    private String label;

    @Basic
    @Column(name = "region", length = 50)
    private String region;

    @Convert(converter = RegionTypeConverter.class)
    @Column(name = "region_type", nullable = false, length = 2, columnDefinition = "BPCHAR")
    private RegionType regionType;

    @Basic
    @Column(name = "locate", length = 50)
    private String locate;

    @Convert(converter = LocateTypeConverter.class)
    @Column(name = "locate_type", nullable = false, length = 2, columnDefinition = "BPCHAR")
    private LocateType locateType;

    @Basic
    @Column(name = "place_number", length = 4)
    private String placeNumber;

    @Basic
    @Column(name = "location_detail", length = 160)
    private String locationDetail;

}
