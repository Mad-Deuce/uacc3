package dms.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Data
@Entity
@Table(name = "dev", schema = "drtu", catalog = "rtubase")
public class DevEntity {
    @Basic
    @Column(name = "id_obj", columnDefinition = "NUMERIC(14,0)")
    private Long idObj;

    @Basic
    @Column(name = "devid", columnDefinition = "NUMERIC(10,0)")
    private Integer devid;

    @Basic
    @Column(name = "num", length = 10)
    private String num;

    @Basic
    @Column(name = "myear", length = 4)
    private String myear;

    @Basic
    @Column(name = "ps", length = 2, columnDefinition = "BPCHAR")
    private String ps;

    @Basic
    @Column(name = "d_create")
    private Date dCreate;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "NUMERIC(10,0)")
    private int id;

    @Basic
    @Column(name = "d_nkip")
    private Date dNkip;

    @Basic
    @Column(name = "d_tkip")
    private Date dTkip;

    @Basic
    @Column(name = "t_zam", columnDefinition = "NUMERIC(5,0)")
    private Integer tZam;

    @Basic
    @Column(name = "obj_code", length = 10)
    private String objCode;

    @Basic
    @Column(name = "ok_send", length = -1, columnDefinition = "BPCHAR")
    private String okSend;

    @Basic
    @Column(name = "opcl", length = -1, columnDefinition = "BPCHAR")
    private String opcl;

    @Basic
    @Column(name = "tid_pr", length = 4)
    private String tidPr;

    @Basic
    @Column(name = "tid_rg", length = 4)
    private String tidRg;

    @Basic
    @Column(name = "scode", length = -1, columnDefinition = "BPCHAR")
    private String scode;

    @Basic
    @Column(name = "detail", length = 160)
    private String detail;


}
