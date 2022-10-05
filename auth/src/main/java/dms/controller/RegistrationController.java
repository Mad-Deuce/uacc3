package dms.controller;

import dms.entity.User;
import dms.exception.NotUniqueUsernameException;
import dms.service.UserService;
import dms.validate.registration.RegistrationInfo;
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
