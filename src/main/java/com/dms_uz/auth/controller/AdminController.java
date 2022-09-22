package com.dms_uz.auth.controller;

import com.dms_uz.auth.dto.UserDTO;
import com.dms_uz.auth.entity.User;
import com.dms_uz.auth.exception.NoEntityException;
import com.dms_uz.auth.exception.NotUniqueUsernameException;
import com.dms_uz.auth.service.UserService;
import com.dms_uz.auth.validate.update.UpdateUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping("/")
    public List<UserDTO> userList() {
        return userService.allUsers().stream().map(this::convert).collect(Collectors.toList());
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable Long id) {
        return convert(userService.findUserById(id));
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.DELETE)
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.PUT)
    @PutMapping("/{id}")
    public void updateUser(@Validated(UpdateUserInfo.class) UserDTO userDTO) {
        userService.updateUser(convert(userDTO));
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.POST)
    @PostMapping("/")
    public void addUser(UserDTO userDTO) {
        userService.addUser(convert(userDTO));
    }

    private User convert(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setPasswordConfirm(userDTO.getPasswordConfirm());
        return user;
    }

    private UserDTO convert(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        return userDTO;
    }

}
