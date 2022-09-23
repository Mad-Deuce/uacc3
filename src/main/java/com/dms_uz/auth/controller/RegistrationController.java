package com.dms_uz.auth.controller;

import com.dms_uz.auth.entity.User;
import com.dms_uz.auth.exception.NotUniqueUsernameException;
import com.dms_uz.auth.service.UserService;
import com.dms_uz.auth.validate.registration.RegistrationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registration")
public class RegistrationController {

    private final UserService userService;

    RegistrationController(@Autowired UserService userService){
        this.userService = userService;
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.POST)
    @PostMapping("/")
    public void addUser(@Validated(RegistrationInfo.class) User userForm) throws NotUniqueUsernameException {
        userService.addUser(userForm);
    }


}
