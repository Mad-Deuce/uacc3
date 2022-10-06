package dms.entity;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonDeserialize(as=DRtuEntity.class)
public abstract class DObjRtuEntity {

    @Id
    @GeneratedValue
    private String id;

    private Integer kodRtu;

}
