package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderItemDto {
    private String productId;
    private int quantity;

}
