package dms.jwt;

import dms.entity.RoleEntity;
import dms.entity.UserEntity;
import dms.standing.data.Status;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class JwtUserDetailsFactory {

    public JwtUserDetailsFactory() {
    }

    public static JwtUserDetails create(UserEntity userEntity) {
        return new JwtUserDetails(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.getSubdivision(),
                userEntity.getPermitCode(),
                userEntity.getStatus().equals(Status.ACTIVE),
                userEntity.getUpdated(),
                mapToGrantedAuthorities(new ArrayList<>(userEntity.getRoles()))
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(List<RoleEntity> roleEntities) {
        return roleEntities.stream()
                .map(role ->
                        new SimpleGrantedAuthority(role.getName())
                ).collect(Collectors.toList());
    }
}
