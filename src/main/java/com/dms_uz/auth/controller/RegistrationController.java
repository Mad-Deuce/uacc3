package com.dms_uz.auth.controller;

import com.dms_uz.auth.entity.User;
import com.dms_uz.auth.exception.NotUniqueUsernameException;
import com.dms_uz.auth.service.UserService;
import com.dms_uz.auth.validate.registration.RegistrationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.POST)
    @PostMapping("/registration")
    public void addUser(@Validated(RegistrationInfo.class) User userForm) throws NotUniqueUsernameException {
        userService.addUser(userForm);
    }


}
