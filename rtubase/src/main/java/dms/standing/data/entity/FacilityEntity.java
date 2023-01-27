package dms.standing.data.entity;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"kodRtu"})
@ToString(of = {"kodRtu"})
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonDeserialize(as = RtdFacilityEntity.class)
public abstract class FacilityEntity implements Serializable {


    @Id
    private String id;

    @Column(columnDefinition = "NUMERIC(1,0)")
    private Integer kodRtu;

//    private String name;

    public abstract String getName();

    public abstract SubdivisionEntity getSubdivision();
}
