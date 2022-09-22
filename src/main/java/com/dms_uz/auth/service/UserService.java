package com.dms_uz.auth.service;

import com.dms_uz.auth.entity.Role;
import com.dms_uz.auth.entity.User;
import com.dms_uz.auth.exception.NoEntityException;
import com.dms_uz.auth.exception.NotUniqueUsernameException;
import com.dms_uz.auth.repository.RoleRepository;
import com.dms_uz.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public boolean isExistsByUsername(String username) {
        return userRepository.findByUsername(username) != null;
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoEntityException("User with the id = " + userId + " does not exist"));
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public void addUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new NotUniqueUsernameException("User with the username = " + user.getUsername() + " already exists");
        }
        user.setId(null);
        user.setRoles(Collections.singleton(new Role(2L, "ROLE_USER")));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void updateUser(User user) {
        User updatedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new NoEntityException("User with the id = " + user.getId() + " does not exist"));
        if (user.getUsername() != null) updatedUser.setUsername(user.getUsername());
        if (user.getPassword() != null) updatedUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(updatedUser);
    }


    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoEntityException("User with the id = " + userId + " does not exist"));
        userRepository.deleteById(userId);
    }

    public List<User> usergtList(Long idMin) {
        return em.createQuery("SELECT u FROM User u WHERE u.id > :paramId", User.class)
                .setParameter("paramId", idMin).getResultList();
    }
}
