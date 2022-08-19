package com.dms_uz.rtubase.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;

@Data
@Entity
@Table(name = "s_dev", schema = "drtu", catalog = "rtubase")
public class SDevEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false, precision = 0)
    private long id;

    @Basic
    @Column(name = "grid", nullable = true, precision = 0)
    private Integer grid;

    @Basic
    @Column(name = "dtype", nullable = true, length = 20)
    private String dtype;

    @Basic
    @Column(name = "mtest", nullable = true, precision = 0)
    private Integer mtest;

    @Basic
    @Column(name = "rtime", nullable = true, precision = 3)
    private BigDecimal rtime;

    @Basic
    @Column(name = "ttime", nullable = true, precision = 3)
    private BigDecimal ttime;

    @Basic
    @Column(name = "narg", nullable = true, precision = 4)
    private BigDecimal narg;

    @Basic
    @Column(name = "ngold", nullable = true, precision = 4)
    private BigDecimal ngold;

    @Basic
    @Column(name = "nplat", nullable = true, precision = 4)
    private BigDecimal nplat;

    @Basic
    @Column(name = "nalk", nullable = true, precision = 4)
    private BigDecimal nalk;

    @Basic
    @Column(name = "name", nullable = true, length = 160)
    private String name;

    @Basic
    @Column(name = "d_create", nullable = true)
    private Date dCreate;

    @Basic
    @Column(name = "plant", nullable = true, length = 160)
    private String plant;

    @Basic
    @Column(name = "scode", nullable = true, length = -1)
    private String scode;

    @Basic
    @Column(name = "tag1", nullable = true, length = 160)
    private String tag1;

    @Basic
    @Column(name = "tag2", nullable = true, length = 160)
    private String tag2;

}
