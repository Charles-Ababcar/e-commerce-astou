// OrderPlacedResponseDTO.java (Nouveau DTO de Réponse)

package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderPlacedResponseDTO {
    private Long orderId;
    private String orderNumber; // Le numéro de commande généré
    private long totalCents;
    private String status;
    private List<OrderItemResponseDTO> items;
}