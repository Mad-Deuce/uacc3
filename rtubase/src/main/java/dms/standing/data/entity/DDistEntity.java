package dms.standing.data.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

@Getter
@EqualsAndHashCode(of = {"dRail", "codeDist"})
@ToString
@NoArgsConstructor
@Entity
@Table(name = "d_dist", schema = "drtu", catalog = "rtubase")
public class DDistEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false, length = 5)
    private String id;

    @ManyToOne
    @JoinColumn(name = "id_rail", referencedColumnName = "id", columnDefinition = "BPCHAR")
    private DRailEntity dRail;

    @Basic
    @Column(name = "dist", length = 8)
    private String dist;

    @Basic
    @Column(name = "name", length = 40)
    private String name;

    @Basic
    @Column(name = "code_dist")
    private BigInteger codeDist;

}
