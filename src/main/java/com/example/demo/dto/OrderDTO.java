package com.example.demo.dto;

import com.example.demo.dto.request.CategoryResponseDTO;
import com.example.demo.model.Client;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Data
public class OrderDTO {
    private Long id;
    private long totalCents;
    private String status;
    private String orderNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- 🚚 NOUVEAUX CHAMPS DE LIVRAISON ---
    private Long deliveryFee;             // Les frais appliqués (ex: 2000)
    private String deliveryZone;          // Le nom de la zone (ex: "Zone 1")
    private String deliveryAddressDetail;

    // Si vous mappez Client :
    private ClientDTO client;

    private CategoryResponseDTO categoryResponseDTO;

    private ShopDTO shop;

    // List<OrderItemResponseDTO> est déjà utilisée dans placeOrder
    private List<OrderItemResponseDTO> items;
}
