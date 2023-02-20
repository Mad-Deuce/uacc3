package dms.entity;



import dms.validate.login.LoginForm;
import dms.validate.login.LoginInfo;
import dms.validate.registration.RegistrationForm;
import dms.validate.registration.RegistrationInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(schema = "public", name = "users")
@LoginForm(groups = LoginInfo.class)
@RegistrationForm(groups = RegistrationInfo.class)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String username;

    @Column(name = "password")
    private String password;

    @Transient
    private String passwordConfirm;

    @Column(name = "subdivision")
    private String subdivision;

    @Column(name = "permit_code")
    private String permitCode;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;


    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

}
