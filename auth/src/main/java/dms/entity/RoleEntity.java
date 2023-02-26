package dms.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(schema = "public", name = "roles")
@Data
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;
}
