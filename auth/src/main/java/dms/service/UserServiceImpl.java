package dms.service;

import dms.entity.RoleEntity;
import dms.entity.UserEntity;
import dms.repository.RoleRepository;
import dms.repository.UserRepository;
import dms.standing.data.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserEntity register(UserEntity userEntity) {
        RoleEntity roleEntity = roleRepository.findByName("ROLE_USER");
        List<RoleEntity> roleEntityList = new ArrayList<>();
        roleEntityList.add(roleEntity);

        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userEntity.setRoles(roleEntityList);
        userEntity.setStatus(Status.ACTIVE);

        UserEntity registeredUser = userRepository.save(userEntity);

        log.info("IN register - user: {} successfully registered", registeredUser);

        return registeredUser;
    }

    @Override
    public List<UserEntity> getAll() {
        List<UserEntity> result = userRepository.findAll();
        log.info("IN getAll - {} users found", result.size());
        return result;
    }

    @Override
    public UserEntity findByLogin(String login) {
        UserEntity result = userRepository.findByUsername(login);
        log.info("IN findByUsername - user: {} found by login: {}", result, login);
        return result;
    }

    @Override
    public UserEntity findById(Long id) {
        UserEntity result = userRepository.findById(id).orElse(null);

        if (result == null) {
            log.warn("IN findById - no user found by id: {}", id);
            return null;
        }

        log.info("IN findById - user: {} found by id: {}", result, id);
        return result;
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
        log.info("IN delete - user with id: {} successfully deleted", id);
    }
}
