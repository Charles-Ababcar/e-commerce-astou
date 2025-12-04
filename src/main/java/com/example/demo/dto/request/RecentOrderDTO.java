package com.example.demo.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RecentOrderDTO {
    private Long orderId;
    private String orderNumber;
    private BigDecimal total; // En FCFA directement
    private String status;
    private LocalDateTime createdAt;
    private String customerName;
    private String customerEmail;
    private String shopName;

}
