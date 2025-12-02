package com.example.demo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CartsItemDTO {
    private Long id;
    private int quantity;
    private int totalCents;
    private ProductDTO product;
}
