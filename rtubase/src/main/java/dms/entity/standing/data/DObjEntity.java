package dms.entity.standing.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Getter
@EqualsAndHashCode(callSuper = true, of = {"dDist", "kodObj"})
@ToString(callSuper = true, of = {"dDist", "kodObj", "nameObj"})
@NoArgsConstructor
@Entity
@Table(name = "d_obj", schema = "drtu", catalog = "rtubase")
public class DObjEntity extends DObjRtuEntity {

    @Basic
    @Column(name = "kod_otd", length = -1, columnDefinition = "BPCHAR")
    private String kodOtd;

    @Basic
    @Column(name = "kod_obkt", nullable = false, columnDefinition = "NUMERIC(3,0)")
    private int kodObkt;

    @Basic
    @Column(name = "kod_obj", length = 3)
    private String kodObj;

    @Basic
    @Column(name = "name_obj", length = 50)
    private String nameObj;

    @Basic
    @Column(name = "kind", length = -1, columnDefinition = "BPCHAR")
    private String kind;

    @Basic
    @Column(name = "cls", length = 2)
    private String cls;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "kod_dist", referencedColumnName = "code_dist", columnDefinition = "NUMERIC"),
            @JoinColumn(name = "kod_dor", referencedColumnName = "id_rail", columnDefinition = "BPCHAR")
    })
    private DDistEntity dDist;

    @Override
    public String getNameObject() {
        return this.nameObj;
    }
}
