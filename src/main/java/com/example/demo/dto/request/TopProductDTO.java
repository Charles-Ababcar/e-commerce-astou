package com.example.demo.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TopProductDTO {
    private Long productId;
    private String productName;
    private String categoryName;
    private String imageUrl;
    private Long totalSold;
    private BigDecimal totalRevenue; // En FCFA

}
