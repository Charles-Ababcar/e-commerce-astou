package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemResponseDTO {

    private Long productId;
    private String productName;
    private Integer quantity;
    private Integer priceCents;
    private Long categoryId;
    private String categoryName;
    private String imageUrl;

}
