package dms.model;


import dms.entity.standing.data.DObjRtuEntity;
import dms.entity.DevObjEntity;
import dms.entity.standing.data.SDevEntity;
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
    @JoinColumn(name = "id_obj")
    private DevObjEntity idObj;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devid", referencedColumnName = "id")
    private SDevEntity sDev;

    @Basic
    @Column(name = "num",  length = 10)
    private String num;

    @Basic
    @Column(name = "myear",  length = 4)
    private String myear;

    @Basic
    @Column(name = "ps",  length = 2)
    private String ps;

    @Basic
    @Column(name = "d_create")
    private Date dCreate;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "d_nkip" )
    private Date dNkip;

    @Basic
    @Column(name = "d_tkip" )
    private Date dTkip;

    @Basic
    @Column(name = "t_zam")
    private Integer tZam;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "obj_code")
    private DObjRtuEntity dObjRtu;

    @Basic
    @Column(name = "ok_send", length = -1)
    private String okSend;

    @Basic
    @Column(name = "opcl",  length = -1)
    private String opcl;

    @Basic
    @Column(name = "tid_pr",  length = 4)
    private String tidPr;

    @Basic
    @Column(name = "tid_rg",  length = 4)
    private String tidRg;

    @Basic
    @Column(name = "scode",  length = -1)
    private String scode;

    @Basic
    @Column(name = "detail",  length = 160)
    private String detail;

    public DevModel(DevModel devModel) {
        this.idObj = devModel.idObj;
        this.sDev = devModel.sDev;
        this.num = devModel.num;
        this.myear = devModel.myear;
        this.ps = devModel.ps;
        this.dCreate = devModel.dCreate;
        this.id = devModel.id;
        this.dNkip = devModel.dNkip;
        this.dTkip = devModel.dTkip;
        this.tZam = devModel.tZam;
        this.dObjRtu = devModel.dObjRtu;
        this.okSend = devModel.okSend;
        this.opcl = devModel.opcl;
        this.tidPr = devModel.tidPr;
        this.tidRg = devModel.tidRg;
        this.scode = devModel.scode;
        this.detail = devModel.detail;
    }

    public DevModel() {

    }
}
