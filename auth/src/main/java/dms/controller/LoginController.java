package dms.controller;


import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.GET)
    @GetMapping("/")
//    public @ResponseBody String greeting() {
//        return "Hello, World";
//    }
    public Principal user(Principal user) {
        return user;
    }

}
