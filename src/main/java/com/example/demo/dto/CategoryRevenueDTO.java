package com.example.demo.dto;

public class CategoryRevenueDTO {
    private String categoryName;
    private Long revenue;

    public CategoryRevenueDTO(String categoryName, Long revenue) {
        this.categoryName = categoryName;
        this.revenue = revenue;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Long getRevenue() {
        return revenue;
    }
}
