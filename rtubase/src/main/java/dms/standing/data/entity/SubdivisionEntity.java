package dms.standing.data.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@EqualsAndHashCode(of = {"railway", "code"})
@ToString
@NoArgsConstructor
@Entity
@Table(name = "d_dist",  catalog = "rtubase")
public class SubdivisionEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false, length = 5)
    private String id;

    @ManyToOne
    @JoinColumn(name = "id_rail", referencedColumnName = "id", columnDefinition = "BPCHAR")
    private RailwayEntity railway;

    @Basic
    @Column(name = "dist", length = 8)
    private String shortName;

    @Basic
    @Column(name = "name", length = 40)
    private String name;

    @Basic
    @Column(name = "code_dist", columnDefinition = "NUMERIC")
    private Integer code;

}
