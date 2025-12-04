package com.example.demo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderItemRequest {

    // Getters and Setters
    private Long productId;
    private int quantity;

}
