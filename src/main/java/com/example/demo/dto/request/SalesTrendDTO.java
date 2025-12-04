package com.example.demo.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SalesTrendDTO {
    private LocalDate date;
    private BigDecimal revenue;
    private Integer sales;
    private Integer customers;
}