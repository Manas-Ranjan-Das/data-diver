package com.example.file_diver.models;

import java.time.LocalDateTime;

import com.example.file_diver.dto.RegisterRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 
    @Column(nullable = false , unique = true)
    private String username;
    @Column(nullable = false )
    private String password;
    private String contact;
    @Column(name = "email" , unique = true)
    private String email;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "created_at")
    private LocalDateTime createdAt ;

    public UserEntity(){
        createdAt = LocalDateTime.now();
    }

    public UserEntity(String username, String password, String contact, String email, String firstName,String lastName){
        this();  // Call the default constructor to initialize createdAt
        this.username = username;
        this.password = password;
        this.contact = contact;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UserEntity(String username, String password, String contact, String email, String firstName,String lastName, LocalDateTime createdAt){
        this( username,password,contact, email,firstName,lastName);
        this.createdAt = createdAt;
    }

    public UserEntity(RegisterRequest registerRequest){
        this(
            registerRequest.getUsername(),
            registerRequest.getPassword(),
            registerRequest.getContact(),
            registerRequest.getEmail(),
            registerRequest.getFirstName(),
            registerRequest.getLastName()
        );
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\": " + id + "," +
                "\"username\": \"" + username + "\"," +
                "\"password\": \"[PROTECTED]\"," +  // Avoid exposing passwords
                "\"contact\": \"" + contact + "\"," +
                "\"email\": \"" + email + "\"," +
                "\"firstName\": \"" + firstName + "\"," +
                "\"lastName\": \"" + lastName + "\"," +
                "\"createdAt\": \"" + createdAt + "\"" +
                "}";
    }


    public String getContact() {
        return contact;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public String getEmail() {
        return email;
    }
    public String getFirstName() {
        return firstName;
    }
    public Long getId() {
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
    public void setContact(String contact) {
        this.contact = contact;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setId(Long id) {
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

}
