package com.dms_uz.auth.controller;


import com.dms_uz.auth.AuthenticationBean;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping("/")
    public Principal user(Principal user) {
        return user;
    }

}
