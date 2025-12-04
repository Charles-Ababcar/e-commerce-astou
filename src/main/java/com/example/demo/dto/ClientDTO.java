package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ClientDTO {
    private Long id;
    private String name;
    private String email;
    private String address;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructeur simplifié pour la conversion
    public ClientDTO(Long id, String name, String email, String address, String phoneNumber, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}