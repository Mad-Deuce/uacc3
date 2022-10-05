package com.dms_uz.auth.controller;

import com.dms_uz.auth.dto.UserDTO;
import com.dms_uz.auth.entity.Role;
import com.dms_uz.auth.entity.User;
import com.dms_uz.auth.service.UserExportService;
import com.dms_uz.auth.service.UserService;
import com.dms_uz.auth.validate.update.UpdateUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
public class AdminController {

    private final UserService userService;
    private final UserExportService userExportService;

    AdminController(@Autowired UserService userService, @Autowired UserExportService userExportService) {
        this.userService = userService;
        this.userExportService = userExportService;
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping("/")
    public List<UserDTO> getAllUsers() {
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
    public void updateUser(@RequestBody @Validated(UpdateUserInfo.class) UserDTO userDTO) {
        userService.updateUser(convert(userDTO));
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.POST)
    @PostMapping("/")
    public UserDTO addUser(@RequestBody UserDTO userDTO) {
       return convert(userService.addUser(convert(userDTO)));
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping("/export")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        userExportService.exportToXlsx(response, userService.allUsers());
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


        String rolesNames = user.getRoles().stream().map(Role::getName).collect(Collectors.joining(", "));
        userDTO.setRoles(rolesNames);

        return userDTO;
    }

}
