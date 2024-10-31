package com.example.file_diver.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class RegisterRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    private String contact;
    private String email;
    private String firstName;
    private String lastName;

    public String getContact() {
        return contact;
    }
    public String getFirstName() {
        return firstName;
    }
    public int getId() {
        return id;
    }
    public String getLastName() {
        return lastName;
    }
    public String getPassword() {
        return password;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    
}
