package com.dms_uz.rtubase.entity;

import javax.persistence.*;
import java.sql.Date;

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

    public Long getIdObj() {
        return idObj;
    }

    public void setIdObj(Long idObj) {
        this.idObj = idObj;
    }

    public Integer getDevid() {
        return devid;
    }

    public void setDevid(Integer devid) {
        this.devid = devid;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getMyear() {
        return myear;
    }

    public void setMyear(String myear) {
        this.myear = myear;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public Date getdCreate() {
        return dCreate;
    }

    public void setdCreate(Date dCreate) {
        this.dCreate = dCreate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getdNkip() {
        return dNkip;
    }

    public void setdNkip(Date dNkip) {
        this.dNkip = dNkip;
    }

    public Date getdTkip() {
        return dTkip;
    }

    public void setdTkip(Date dTkip) {
        this.dTkip = dTkip;
    }

    public Integer gettZam() {
        return tZam;
    }

    public void settZam(Integer tZam) {
        this.tZam = tZam;
    }

    public String getObjCode() {
        return objCode;
    }

    public void setObjCode(String objCode) {
        this.objCode = objCode;
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

    public String getTidPr() {
        return tidPr;
    }

    public void setTidPr(String tidPr) {
        this.tidPr = tidPr;
    }

    public String getTidRg() {
        return tidRg;
    }

    public void setTidRg(String tidRg) {
        this.tidRg = tidRg;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DevEntity devEntity = (DevEntity) o;

        if (id != devEntity.id) return false;
        if (idObj != null ? !idObj.equals(devEntity.idObj) : devEntity.idObj != null) return false;
        if (devid != null ? !devid.equals(devEntity.devid) : devEntity.devid != null) return false;
        if (num != null ? !num.equals(devEntity.num) : devEntity.num != null) return false;
        if (myear != null ? !myear.equals(devEntity.myear) : devEntity.myear != null) return false;
        if (ps != null ? !ps.equals(devEntity.ps) : devEntity.ps != null) return false;
        if (dCreate != null ? !dCreate.equals(devEntity.dCreate) : devEntity.dCreate != null) return false;
        if (dNkip != null ? !dNkip.equals(devEntity.dNkip) : devEntity.dNkip != null) return false;
        if (dTkip != null ? !dTkip.equals(devEntity.dTkip) : devEntity.dTkip != null) return false;
        if (tZam != null ? !tZam.equals(devEntity.tZam) : devEntity.tZam != null) return false;
        if (objCode != null ? !objCode.equals(devEntity.objCode) : devEntity.objCode != null) return false;
        if (okSend != null ? !okSend.equals(devEntity.okSend) : devEntity.okSend != null) return false;
        if (opcl != null ? !opcl.equals(devEntity.opcl) : devEntity.opcl != null) return false;
        if (tidPr != null ? !tidPr.equals(devEntity.tidPr) : devEntity.tidPr != null) return false;
        if (tidRg != null ? !tidRg.equals(devEntity.tidRg) : devEntity.tidRg != null) return false;
        if (scode != null ? !scode.equals(devEntity.scode) : devEntity.scode != null) return false;
        if (detail != null ? !detail.equals(devEntity.detail) : devEntity.detail != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idObj != null ? idObj.hashCode() : 0;
        result = 31 * result + (devid != null ? devid.hashCode() : 0);
        result = 31 * result + (num != null ? num.hashCode() : 0);
        result = 31 * result + (myear != null ? myear.hashCode() : 0);
        result = 31 * result + (ps != null ? ps.hashCode() : 0);
        result = 31 * result + (dCreate != null ? dCreate.hashCode() : 0);
        result = 31 * result + id;
        result = 31 * result + (dNkip != null ? dNkip.hashCode() : 0);
        result = 31 * result + (dTkip != null ? dTkip.hashCode() : 0);
        result = 31 * result + (tZam != null ? tZam.hashCode() : 0);
        result = 31 * result + (objCode != null ? objCode.hashCode() : 0);
        result = 31 * result + (okSend != null ? okSend.hashCode() : 0);
        result = 31 * result + (opcl != null ? opcl.hashCode() : 0);
        result = 31 * result + (tidPr != null ? tidPr.hashCode() : 0);
        result = 31 * result + (tidRg != null ? tidRg.hashCode() : 0);
        result = 31 * result + (scode != null ? scode.hashCode() : 0);
        result = 31 * result + (detail != null ? detail.hashCode() : 0);
        return result;
    }
}
