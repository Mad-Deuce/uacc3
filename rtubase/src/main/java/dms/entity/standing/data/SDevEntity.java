package dms.entity.standing.data;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;

@Setter
@Getter
@EqualsAndHashCode(of = {"grid", "dtype"})
@ToString(of = {"grid", "dtype"})
@NoArgsConstructor
@Entity
@Table(name = "s_dev", schema = "drtu", catalog = "rtubase")
public class SDevEntity {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "NUMERIC")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grid", referencedColumnName = "grid")
    private SDevgrpEntity grid;

    @Basic
    @Column(name = "dtype", length = 20)
    private String dtype;

    @Basic
    @Column(name = "mtest", columnDefinition = "NUMERIC")
    private Integer mtest;

    @Basic
    @Column(name = "rtime", precision = 3)
    private BigDecimal rtime;

    @Basic
    @Column(name = "ttime", precision = 3)
    private BigDecimal ttime;

    @Basic
    @Column(name = "narg", precision = 4)
    private BigDecimal narg;

    @Basic
    @Column(name = "ngold", precision = 4)
    private BigDecimal ngold;

    @Basic
    @Column(name = "nplat", precision = 4)
    private BigDecimal nplat;

    @Basic
    @Column(name = "nalk", precision = 4)
    private BigDecimal nalk;

    @Basic
    @Column(name = "name", length = 160)
    private String name;

    @Basic
    @Column(name = "d_create")
    private Date dCreate;

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
