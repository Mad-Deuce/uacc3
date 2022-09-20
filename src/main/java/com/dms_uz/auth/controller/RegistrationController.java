package com.dms_uz.auth.controller;

import com.dms_uz.auth.entity.User;
import com.dms_uz.auth.service.UserService;
import com.dms_uz.auth.validate.LoginInfo;
import com.dms_uz.auth.validate.RegistrationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/auth")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.POST)
    @PostMapping("/registration")
    public boolean addUser(@ModelAttribute("userForm") @Validated(RegistrationInfo.class) User userForm) {
        return userService.saveUser(userForm);
    }


}
