package com.dms_uz.rtubase.entity;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "d_rtu", schema = "drtu", catalog = "rtubase")
public class DRtuEntity extends DObjRtuEntity{

//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Id
//    @Column(name = "id", nullable = false, length = 5)
//    private String id;

    @Basic
    @Column(name = "id_rail", nullable = true, length = -1)
    private String idRail;
    @Basic
    @Column(name = "name", nullable = true, length = 40)
    private String name;


    @Transient
    private String nameObj;
//    @Basic
//    @Column(name = "kod_rtu", nullable = true, precision = 0)
//    private Integer kodRtu;

    @Basic
    @Column(name = "kod_did", nullable = true, precision = 0)
    private BigInteger kodDid;

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }

    public String getIdRail() {
        return idRail;
    }

    public void setIdRail(String idRail) {
        this.idRail = idRail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameObj() {
        return nameObj;
    }

    public void setNameObj(String nameObj) {
        this.nameObj = nameObj;
    }
//    public Integer getKodRtu() {
//        return kodRtu;
//    }
//
//    public void setKodRtu(Integer kodRtu) {
//        this.kodRtu = kodRtu;
//    }

    public BigInteger getKodDid() {
        return kodDid;
    }

    public void setKodDid(BigInteger kodDid) {
        this.kodDid = kodDid;
    }

}
