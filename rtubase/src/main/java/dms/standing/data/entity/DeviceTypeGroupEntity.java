package dms.standing.data.entity;

import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id", "name"})
@NoArgsConstructor
@Entity
@Table(name = "s_devgrp",  catalog = "rtubase")
public class DeviceTypeGroupEntity {

    @Id
    @Column(name = "grid", nullable = false, columnDefinition = "NUMERIC")
    private Integer id;

    @Basic
    @Column(name = "name", length = 160)
    private String name;

}
