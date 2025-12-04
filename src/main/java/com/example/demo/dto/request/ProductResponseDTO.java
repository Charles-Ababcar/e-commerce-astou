package com.example.demo.dto.request;

import com.example.demo.dto.ShopResponseDTO;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Data
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String description;

    private double rating;

    private String imageUrl;
    private int priceCents;
    private int stock;
    private CategoryResponseDTO categoryResponseDTO;
    private ShopResponseDTO cShopResponseDTO;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;



}
