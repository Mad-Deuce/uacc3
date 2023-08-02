package dms.standing.data.entity;

import lombok.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;

@Setter
@Getter
@EqualsAndHashCode(of = {"group", "name"})
@ToString(of = {"group", "name"})
@NoArgsConstructor
@Entity
@Proxy(lazy=false)
@Table(name = "s_dev", catalog = "rtubase")
public class DeviceTypeEntity {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "NUMERIC")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "grid", referencedColumnName = "grid")
    private DeviceTypeGroupEntity group;

    @Basic
    @Column(name = "dtype", length = 20)
    private String name;

    @Basic
    @Column(name = "mtest", columnDefinition = "NUMERIC")
    private Integer replacementPeriod;

    @Basic
    @Column(name = "rtime", precision = 3)
    private BigDecimal adjustmentTime;

    @Basic
    @Column(name = "ttime", precision = 3)
    private BigDecimal acceptanceTime;

    @Basic
    @Column(name = "narg", precision = 4)
    private BigDecimal argentNorm;

    @Basic
    @Column(name = "ngold", precision = 4)
    private BigDecimal goldNorm;

    @Basic
    @Column(name = "nplat", precision = 4)
    private BigDecimal platinumNorm;

    @Basic
    @Column(name = "nalk", precision = 4)
    private BigDecimal alcoholNorm;

    @Basic
    @Column(name = "name", length = 160)
    private String nameOld;

    @Basic
    @Column(name = "d_create")
    private Date createDate;

    @Basic
    @Column(name = "plant", length = 160)
    private String plant;

    @Basic
    @Column(name = "scode", length = -1, columnDefinition = "BPCHAR")
    private String scode;

    @Basic
    @Column(name = "tag1", length = 160)
    private String tag1;

    @Basic
    @Column(name = "tag2", length = 160)
    private String tag2;

}
