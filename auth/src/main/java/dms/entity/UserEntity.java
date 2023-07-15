package dms.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(schema = "public", name = "users")
@Data
@EqualsAndHashCode(callSuper = false)
public class UserEntity extends BaseEntity {

    @Column(name = "name")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "subdivision")
    private String subdivision;

    @Column(name = "permit_code")
    private String permitCode;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(schema = "public", name = "users_roles",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "roles_id", referencedColumnName = "id")})
    private List<RoleEntity> roles;
}
