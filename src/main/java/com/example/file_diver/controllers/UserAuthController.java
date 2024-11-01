package com.example.file_diver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.file_diver.dto.LoginRequest;
import com.example.file_diver.dto.RegisterRequest;
import com.example.file_diver.services.UserService;

@RestController
@RequestMapping(path = "user")
public class UserAuthController {
    private final UserService userService ;

    @Autowired
    public UserAuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "login")
    ResponseEntity<String> login (@RequestBody LoginRequest loginRequest){
        return userService.validateLogin(loginRequest);
    }

    @PostMapping(path = "register")
    ResponseEntity<?> register (@RequestBody RegisterRequest request){
        return userService.registerNewUser(request);
    }


}
