package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShopRequestDTO {

    @NotBlank(message = "Le nom du shop est requis")
    private String name;
    private String description;
    private String phoneNumber;
    private String address;
    private  String email;
    private Boolean isActive;
}
