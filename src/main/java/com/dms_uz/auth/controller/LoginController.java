package com.dms_uz.auth.controller;


import com.dms_uz.auth.AuthenticationBean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class LoginController {

    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping("/api/user")
    public Principal user(Principal user) {
        return user;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(path = "/api/basicauth")
    public AuthenticationBean basicauth() {
        return new AuthenticationBean("You are authenticated");
    }


}
