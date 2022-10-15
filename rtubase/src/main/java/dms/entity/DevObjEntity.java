package dms.entity;

import dms.converter.LocateTypeConverter;
import dms.converter.RegionTypeConverter;
import dms.dock.val.LocateType;
import dms.dock.val.RegionType;
import dms.entity.standing.data.DObjEntity;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "dev_obj", schema = "drtu", catalog = "rtubase")
public class DevObjEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "NUMERIC")
    private long id;

//    @Basic
//    @Column(name = "obj_code", length = 10)
//    private String objCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "obj_code", referencedColumnName = "id", columnDefinition = "BPCHAR")
    private DObjEntity objCode;

    @Basic
    @Column(name = "locate", length = 50)
    private String locate;

    @Basic
    @Column(name = "nplace",  length = 4)
    private String nplace;

    @Basic
    @Column(name = "nshem",  length = 50)
    private String nshem;

//    @Basic
//    @Column(name = "locate_t",  length = 2, columnDefinition = "BPCHAR")
//    private String locateT;

    @Convert(converter = LocateTypeConverter.class)
    @Column(name = "locate_t", nullable = false, length = 2, columnDefinition = "BPCHAR")
    private LocateType locateType;

    @Basic
    @Column(name = "region",  length = 50)
    private String region;

//    @Basic
//    @Column(name = "region_t",  length = 2, columnDefinition = "BPCHAR")
//    private String regionType;

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
