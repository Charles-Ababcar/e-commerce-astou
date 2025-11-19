package com.example.demo.dto.request;

import lombok.Data;

@Data
public class ShopRequestDTO {
    private String name;
    private String description;
    private String phoneNumber;
    private String address;
    private  String email;
    private Boolean isActive;
}
