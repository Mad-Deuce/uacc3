package dms.entity.standing.data;

import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@EqualsAndHashCode(of = {"grid"})
@ToString(of = {"grid", "name"})
@NoArgsConstructor
@Entity
@Table(name = "s_devgrp", schema = "drtu", catalog = "rtubase")
public class SDevgrpEntity {

    @Id
    @Column(name = "grid", nullable = false, columnDefinition = "NUMERIC")
    private int grid;

    @Basic
    @Column(name = "name", length = 160)
    private String name;

}
