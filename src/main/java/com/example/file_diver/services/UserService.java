package com.example.file_diver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Optional;

import com.example.file_diver.dto.LoginRequest;
import com.example.file_diver.dto.RegisterRequest;
import com.example.file_diver.models.UserEntity;
import com.example.file_diver.repositories.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService (UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public ResponseEntity<String> validateLogin (LoginRequest loginRequest){
        Optional<UserEntity> user = userRepository.findByUsername(loginRequest.getUsername());
        if(user.isEmpty()){
            return new ResponseEntity<String>("Username not found", HttpStatus.NOT_FOUND);
        }
        else if (user.get().getPassword().equals(loginRequest.getPassword())){
            return new ResponseEntity<String>( user.get().toString() , HttpStatus.OK);
        }
        else{
            return new ResponseEntity<String>("Incorrect Password", HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<?> registerNewUser (RegisterRequest registerRequest){
        Optional<UserEntity> user = userRepository.findByUsername(registerRequest.getUsername());
        if(!user.isEmpty()){
            return new ResponseEntity<String>("Username alrady exists", HttpStatus.CONFLICT);
        }
        else{
            UserEntity newUser = new UserEntity(registerRequest);
            userRepository.save(newUser);
            return new ResponseEntity<UserEntity>(newUser, HttpStatus.CREATED);
        }
    }



}
