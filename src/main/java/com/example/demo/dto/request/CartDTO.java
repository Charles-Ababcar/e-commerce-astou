package com.example.demo.dto.request;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CartDTO {
    private Long id;
    private boolean ordered;
    private String createdAt;
    private String updatedAt;
    private ShopDTO shop; // <-- objet ShopDTO
    private int totalPriceCents;
    private List<CartItemDTO> items;


}
