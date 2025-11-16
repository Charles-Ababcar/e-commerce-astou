package com.example.demo.dto;

import java.util.List;

public class UserProfileDto {
    private String username;
    private List<String> roles;

    public UserProfileDto(String username, List<String> roles) {
        this.username = username;
        this.roles = roles;
    }
}
