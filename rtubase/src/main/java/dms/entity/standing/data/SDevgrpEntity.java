package dms.entity.standing.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Getter
@EqualsAndHashCode(of = {"grid", "name"})
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
