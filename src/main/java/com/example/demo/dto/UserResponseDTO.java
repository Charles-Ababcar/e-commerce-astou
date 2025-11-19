package com.example.demo.dto;

import com.example.demo.model.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class UserResponseDTO {

    private Long id;
    private String name;
    private String username;
    private String email;
    private User.Role role;
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
