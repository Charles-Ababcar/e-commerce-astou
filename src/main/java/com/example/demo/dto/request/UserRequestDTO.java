package com.example.demo.dto.request;

import com.example.demo.model.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class UserRequestDTO {
    private String name;
    private String username;
    private String email;
    private String password;
    private User.Role role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
