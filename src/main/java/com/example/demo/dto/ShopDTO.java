package com.example.demo.dto;

import lombok.Data;

@Data
public class ShopDTO {
    private Long id;
    private String name;
    // Ajoutez d'autres champs pertinents de l'entité Shop si nécessaire
    
    // Constructeur simplifié
    public ShopDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}