package dms.standing.data.entity;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
@JsonDeserialize(as = RtuObjectEntity.class)
public abstract class ObjectEntity implements Serializable {

    @Id
    @GeneratedValue
    private String id;

    @Column(columnDefinition = "NUMERIC(1,0)")
    private Integer kodRtu;

    public abstract String getName();

}
