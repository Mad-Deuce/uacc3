package com.dms_uz.rtubase.entity;

import javax.persistence.*;

@Entity
@Table(name = "dev_obj", schema = "drtu", catalog = "rtubase")
public class DevObjEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false, precision = 0)
    private long id;
    @Basic
    @Column(name = "obj_code", nullable = true, length = 10)
    private String objCode;
    @Basic
    @Column(name = "locate", nullable = true, length = 50)
    private String locate;
    @Basic
    @Column(name = "nplace", nullable = true, length = 4)
    private String nplace;
    @Basic
    @Column(name = "nshem", nullable = true, length = 50)
    private String nshem;
    @Basic
    @Column(name = "locate_t", nullable = true, length = 2)
    private String locateT;
    @Basic
    @Column(name = "region", nullable = true, length = 50)
    private String region;
    @Basic
    @Column(name = "region_t", nullable = true, length = 2)
    private String regionT;
    @Basic
    @Column(name = "ok_send", nullable = true, length = -1)
    private String okSend;
    @Basic
    @Column(name = "opcl", nullable = true, length = -1)
    private String opcl;
    @Basic
    @Column(name = "scode", nullable = true, length = -1)
    private String scode;
    @Basic
    @Column(name = "detail", nullable = true, length = 160)
    private String detail;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getObjCode() {
        return objCode;
    }

    public void setObjCode(String objCode) {
        this.objCode = objCode;
    }

    public String getLocate() {
        return locate;
    }

    public void setLocate(String locate) {
        this.locate = locate;
    }

    public String getNplace() {
        return nplace;
    }

    public void setNplace(String nplace) {
        this.nplace = nplace;
    }

    public String getNshem() {
        return nshem;
    }

    public void setNshem(String nshem) {
        this.nshem = nshem;
    }

    public String getLocateT() {
        return locateT;
    }

    public void setLocateT(String locateT) {
        this.locateT = locateT;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegionT() {
        return regionT;
    }

    public void setRegionT(String regionT) {
        this.regionT = regionT;
    }

    public String getOkSend() {
        return okSend;
    }

    public void setOkSend(String okSend) {
        this.okSend = okSend;
    }

    public String getOpcl() {
        return opcl;
    }

    public void setOpcl(String opcl) {
        this.opcl = opcl;
    }

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

}
