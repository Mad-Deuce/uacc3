package dms.standing.data.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, of = {"subdivision", "codeStr"})
@ToString(callSuper = true, of = {"subdivision", "codeStr", "name"})
@NoArgsConstructor
@Entity
@Table(name = "d_obj",  catalog = "rtubase")
@JsonDeserialize(as = LineFacilityEntity.class)
public class LineFacilityEntity extends FacilityEntity {

    @Basic
    @Column(name = "kod_otd", length = -1, columnDefinition = "BPCHAR")
    private String affiliateCode;

    @Basic
    @Column(name = "kod_obkt", nullable = false, columnDefinition = "NUMERIC(3,0)")
    private Integer codeNum;

    @Basic
    @Column(name = "kod_obj", length = 3)
    private String codeStr;

    @Basic
    @Column(name = "name_obj", length = 50)
    private String name;

    @Basic
    @Column(name = "kind", length = -1, columnDefinition = "BPCHAR")
    private String kind;

    @Basic
    @Column(name = "cls", length = 2)
    private String cls;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "kod_dor", referencedColumnName = "id_rail", columnDefinition = "BPCHAR"),
            @JoinColumn(name = "kod_dist", referencedColumnName = "code_dist", columnDefinition = "NUMERIC")
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
