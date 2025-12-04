package com.example.demo.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RecentCustomerDTO {
    private Long userId;
    private String name;
    private String email;
    private LocalDate joinedDate;
    private Long orderCount;
}