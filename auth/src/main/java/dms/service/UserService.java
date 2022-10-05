package dms.service;

import dms.entity.Role;
import dms.entity.User;
import dms.exception.NoEntityException;
import dms.exception.NotUniqueUsernameException;
import dms.repository.RoleRepository;
import dms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return Optional.of(userRepository.findByUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("User with the id = " + username + " not found"));
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoEntityException("User with the id = " + userId + " not found"));
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public User addUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new NotUniqueUsernameException("User with the username = " + user.getUsername() + " already exists");
        }
        user.setId(null);
        user.setRoles(Collections.singleton(new Role(2L, "ROLE_USER")));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void updateUser(User user) {
        User updatedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new NoEntityException("User with the id = " + user.getId() + " not found"));
        if (user.getUsername() != null) updatedUser.setUsername(user.getUsername());
        if (user.getPassword() != null) updatedUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(updatedUser);
    }

    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoEntityException("User with the id = " + userId + " not found"));
        userRepository.deleteById(userId);
    }

}
