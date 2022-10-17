package dms.entity.standing.data;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(of = {"code"})
@ToString(of = {"code", "name"})
@NoArgsConstructor
@Entity
@Table(name = "d_rail", schema = "drtu", catalog = "rtubase")
public class DRailEntity {

    @Id
    @Column(name = "id", nullable = false, length = -1, columnDefinition = "BPCHAR")
    private String id;

    @Basic
    @Column(name = "name", length = 40)
    private String name;

    @Basic
    @Column(name = "code", length = -1, columnDefinition = "BPCHAR")
    private String code;


}
