package com.example.demo.dto.request;

import com.example.demo.dto.ShopResponseDTO;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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
    // Nouveaux champs pour les tailles et couleurs
    private List<String> availableColors;
    private List<String> availableSizes;
    private CategoryResponseDTO categoryResponseDTO;
    private ShopResponseDTO cShopResponseDTO;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;



}
