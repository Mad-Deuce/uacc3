package dms.standing.data.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import javax.persistence.*;


@Getter
@Setter
@EqualsAndHashCode(callSuper = true, of = {"subdivision"})
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "d_rtu",  catalog = "rtubase")
@JsonDeserialize(as = RtdFacilityEntity.class)
public class RtdFacilityEntity extends FacilityEntity {

    @Basic
    @Column(name = "name", length = 40)
    private String name;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "id_rail", referencedColumnName = "id_rail", columnDefinition = "BPCHAR"),
            @JoinColumn(name = "kod_did", referencedColumnName = "code_dist", columnDefinition = "NUMERIC")
    })
    private SubdivisionEntity subdivision;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public SubdivisionEntity getSubdivision(){
        return this.subdivision;
    }
}
