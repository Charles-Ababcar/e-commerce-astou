package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddItemRequest {
    private Long productId;
    private Integer quantity;

}
