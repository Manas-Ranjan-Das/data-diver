package com.example.file_diver.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    
    @GetMapping( path = "login" )
    void getLogin (){
        
    }

}
