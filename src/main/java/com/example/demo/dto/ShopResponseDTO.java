package com.example.demo.dto;

import lombok.Data;

@Data
public class ShopResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String address;
    private String phoneNumber;
    private String email;
    private String imageUrl;
    private Boolean isActive;
}
