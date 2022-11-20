package dms.standing.data.entity;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@EqualsAndHashCode(of = {"kodRtu"})
@ToString(of = {"kodRtu"})
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonDeserialize(as = RtuObjectEntity.class)
public abstract class ObjectEntity {

    @Id
    @GeneratedValue
    private String id;

    @Column(columnDefinition = "NUMERIC(1,0)")
    private Integer kodRtu;

    public abstract String getName();

}
