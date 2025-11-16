package com.example.demo.dto;

import java.util.List;

public class UserProfileDto {
    private String username;
    private String email; // si tu as
    private List<String> roles;

    public UserProfileDto(String username, String email, List<String> roles) {
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
