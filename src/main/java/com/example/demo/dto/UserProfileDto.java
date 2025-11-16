package com.example.demo.dto;

import java.util.List;

public class UserProfileDto {
    private String username;
    private String email; // si tu as un champ email
    private List<String> roles;

    public UserProfileDto(String username, String email, List<String> roles) {
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    // getters et setters
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public List<String> getRoles() { return roles; }
}
