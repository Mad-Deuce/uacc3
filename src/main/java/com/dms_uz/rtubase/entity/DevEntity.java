package com.dms_uz.rtubase.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Data
@Entity
@Table(name = "dev", schema = "drtu", catalog = "rtubase")
public class DevEntity {
    @Basic
    @Column(name = "id_obj", nullable = true, precision = 0)
    private Long idObj;

    @Basic
    @Column(name = "devid", nullable = true, precision = 0)
    private Integer devid;

    @Basic
    @Column(name = "num", nullable = true, length = 10)
    private String num;

    @Basic
    @Column(name = "myear", nullable = true, length = 4)
    private String myear;

    @Basic
    @Column(name = "ps", nullable = true, length = 2)
    private String ps;

    @Basic
    @Column(name = "d_create", nullable = true)
    private Date dCreate;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false, precision = 0)
    private int id;

    @Basic
    @Column(name = "d_nkip", nullable = true)
    private Date dNkip;

    @Basic
    @Column(name = "d_tkip", nullable = true)
    private Date dTkip;

    @Basic
    @Column(name = "t_zam", nullable = true, precision = 0)
    private Integer tZam;

    @Basic
    @Column(name = "obj_code", nullable = true, length = 10)
    private String objCode;

    @Basic
    @Column(name = "ok_send", nullable = true, length = -1)
    private String okSend;

    @Basic
    @Column(name = "opcl", nullable = true, length = -1)
    private String opcl;

    @Basic
    @Column(name = "tid_pr", nullable = true, length = 4)
    private String tidPr;

    @Basic
    @Column(name = "tid_rg", nullable = true, length = 4)
    private String tidRg;

    @Basic
    @Column(name = "scode", nullable = true, length = -1)
    private String scode;

    @Basic
    @Column(name = "detail", nullable = true, length = 160)
    private String detail;


}
