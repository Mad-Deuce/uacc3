package dms.entity.standing.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;


@Getter
@EqualsAndHashCode(callSuper = true, of = {"dDist"})
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "d_rtu", schema = "drtu", catalog = "rtubase")
public class DRtuEntity extends DObjRtuEntity {

    @Basic
    @Column(name = "name", length = 40)
    private String name;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "kod_did", referencedColumnName = "code_dist", columnDefinition = "NUMERIC"),
            @JoinColumn(name = "id_rail", referencedColumnName = "id_rail", columnDefinition = "BPCHAR")
    })
    private DDistEntity dDist;

}
