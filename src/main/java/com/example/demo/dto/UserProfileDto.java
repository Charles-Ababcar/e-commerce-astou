package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Data
@AllArgsConstructor
public class UserProfileDto {
    private Long id;
    private String username;
    private String email;
   private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> roles;

}

