package dms.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(schema="public",name = "users")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "subdivision")
    private String subdivision;

    @Column(name = "permit_code")
    private String permitCode;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity roleEntity;
}
