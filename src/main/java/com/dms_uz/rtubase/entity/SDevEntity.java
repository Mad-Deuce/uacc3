package com.dms_uz.rtubase.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getGrid() {
        return grid;
    }

    public void setGrid(Integer grid) {
        this.grid = grid;
    }

    public String getDtype() {
        return dtype;
    }

    public void setDtype(String dtype) {
        this.dtype = dtype;
    }

    public Integer getMtest() {
        return mtest;
    }

    public void setMtest(Integer mtest) {
        this.mtest = mtest;
    }

    public BigDecimal getRtime() {
        return rtime;
    }

    public void setRtime(BigDecimal rtime) {
        this.rtime = rtime;
    }

    public BigDecimal getTtime() {
        return ttime;
    }

    public void setTtime(BigDecimal ttime) {
        this.ttime = ttime;
    }

    public BigDecimal getNarg() {
        return narg;
    }

    public void setNarg(BigDecimal narg) {
        this.narg = narg;
    }

    public BigDecimal getNgold() {
        return ngold;
    }

    public void setNgold(BigDecimal ngold) {
        this.ngold = ngold;
    }

    public BigDecimal getNplat() {
        return nplat;
    }

    public void setNplat(BigDecimal nplat) {
        this.nplat = nplat;
    }

    public BigDecimal getNalk() {
        return nalk;
    }

    public void setNalk(BigDecimal nalk) {
        this.nalk = nalk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getdCreate() {
        return dCreate;
    }

    public void setdCreate(Date dCreate) {
        this.dCreate = dCreate;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }

    public String getTag1() {
        return tag1;
    }

    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public String getTag2() {
        return tag2;
    }

    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }


}
