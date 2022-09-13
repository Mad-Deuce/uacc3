package com.dms_uz.rtubase.model;


import com.dms_uz.rtubase.entity.DObjRtuEntity;
import com.dms_uz.rtubase.entity.DevObjEntity;
import com.dms_uz.rtubase.entity.SDevEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Data
@Accessors(chain = true)
@Entity
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
@Table(name = "dev", schema = "drtu", catalog = "rtubase")
public class DevModel implements Serializable {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_obj", nullable = true)
    private DevObjEntity idObj;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devid", referencedColumnName = "id")
    private SDevEntity sDev;

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
    private Long id;

    @Basic
    @Column(name = "d_nkip", nullable = true)
    private Date dNkip;

    @Basic
    @Column(name = "d_tkip", nullable = true)
    private Date dTkip;

    @Basic
    @Column(name = "t_zam", nullable = true, precision = 0)
    private Integer tZam;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "obj_code")
//    private DObjRtuEntity dObjRtu;

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
