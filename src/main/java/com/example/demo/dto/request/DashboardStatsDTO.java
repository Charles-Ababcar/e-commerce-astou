package com.example.demo.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class DashboardStatsDTO {
    private BigDecimal totalRevenue; // En FCFA directement
    private Long totalOrders;
    private Long totalCustomers;
    private Long totalProducts;
    private Long newCustomers;
    private Double conversionRate;
    private BigDecimal averageOrderValue; // En FCFA directement
    private Map<String, Double> trends;
    private BigDecimal totalDeliveryRevenue;
}
