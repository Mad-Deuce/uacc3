package dms.entity;


import dms.converter.StatusConverter;
import dms.dock.val.Status;
import dms.entity.standing.data.DObjRtuEntity;
import dms.entity.standing.data.SDevEntity;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
//import java.sql.Date;

@Setter
@Getter
@EqualsAndHashCode(of = {"sDev", "num", "myear"})
@ToString(of = {"sDev", "num", "myear"})
@NoArgsConstructor
@Entity
@Table(name = "dev", schema = "drtu", catalog = "rtubase")
public class DevEntity implements Serializable {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_obj", referencedColumnName = "id", columnDefinition = "NUMERIC(14,0)")
    private DevObjEntity devObj;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devid", referencedColumnName = "id")
    private SDevEntity sDev;

    @Basic
    @Column(name = "num", length = 10)
    private String num;

    @Basic
    @Column(name = "myear", length = 4)
    private String myear;

    @Convert(converter = StatusConverter.class)
    @Column(name = "ps", nullable = false, length = 2, columnDefinition = "BPCHAR")
    private Status status;

    @Basic
    @Column(name = "d_create")
    private Date dCreate;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "NUMERIC(10,0)")
    @Setter
    private Long id;

    @Basic
    @Column(name = "d_nkip")
    private Date dNkip;

    @Basic
    @Column(name = "d_tkip")
    private Date dTkip;

    @Basic
    @Column(name = "t_zam", columnDefinition = "NUMERIC(5,0)")
    private Integer tZam;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "obj_code")
    private DObjRtuEntity dObjRtu;

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
