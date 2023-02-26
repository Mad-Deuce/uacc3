package dms.controller;


import dms.dto.AuthRequest;
import dms.dto.AuthResponse;
import dms.entity.UserEntity;
import dms.jwt.JwtProvider;
import dms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProvider jwtProvider;


    @CrossOrigin(origins = "http://localhost:4200", methods = RequestMethod.PUT)
    @PostMapping(value = "/auth/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AuthResponse auth(@RequestBody AuthRequest request) {
        UserEntity userEntity = userService.findByLoginAndPassword(request.getLogin(), request.getPassword());
        String token = jwtProvider.generateToken(userEntity.getLogin());
        return new AuthResponse(token);
    }
}
