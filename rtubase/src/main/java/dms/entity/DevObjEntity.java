package dms.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "dev_obj", schema = "drtu", catalog = "rtubase")
public class DevObjEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private long id;

    @Basic
    @Column(name = "obj_code", length = 10)
    private String objCode;

    @Basic
    @Column(name = "locate", length = 50)
    private String locate;

    @Basic
    @Column(name = "nplace",  length = 4)
    private String nplace;

    @Basic
    @Column(name = "nshem",  length = 50)
    private String nshem;

    @Basic
    @Column(name = "locate_t",  length = 2)
    private String locateT;

    @Basic
    @Column(name = "region",  length = 50)
    private String region;

    @Basic
    @Column(name = "region_t",  length = 2)
    private String regionT;

    @Basic
    @Column(name = "ok_send",  length = -1)
    private String okSend;

    @Basic
    @Column(name = "opcl",  length = -1)
    private String opcl;

    @Basic
    @Column(name = "scode",  length = -1)
    private String scode;

    @Basic
    @Column(name = "detail",  length = 160)
    private String detail;
}
