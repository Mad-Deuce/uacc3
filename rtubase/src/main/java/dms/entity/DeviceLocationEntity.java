package dms.entity;

import dms.standing.data.converter.LocateTypeConverter;
import dms.standing.data.converter.RegionTypeConverter;
import dms.standing.data.dock.val.LocateType;
import dms.standing.data.dock.val.RegionType;
import dms.standing.data.entity.LineObjectEntity;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "dev_obj", schema = "drtu", catalog = "rtubase")
public class DeviceLocationEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "NUMERIC")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "obj_code", referencedColumnName = "id", columnDefinition = "BPCHAR")
    private LineObjectEntity object;

    @Basic
    @Column(name = "locate", length = 50)
    private String locate;

    @Basic
    @Column(name = "nplace",  length = 4)
    private String placeNumber;

    @Basic
    @Column(name = "nshem",  length = 50)
    private String description;

    @Convert(converter = LocateTypeConverter.class)
    @Column(name = "locate_t", nullable = false, length = 2, columnDefinition = "BPCHAR")
    private LocateType locateType;

    @Basic
    @Column(name = "region",  length = 50)
    private String region;

    @Convert(converter = RegionTypeConverter.class)
    @Column(name = "region_t", nullable = false, length = 2, columnDefinition = "BPCHAR")
    private RegionType regionType;


    @Basic
    @Column(name = "ok_send",  length = -1, columnDefinition = "BPCHAR")
    private String okSend;

    @Basic
    @Column(name = "opcl",  length = -1, columnDefinition = "BPCHAR")
    private String opcl;

    @Basic
    @Column(name = "scode",  length = -1, columnDefinition = "BPCHAR")
    private String scode;

    @Basic
    @Column(name = "detail",  length = 160)
    private String detail;
}
