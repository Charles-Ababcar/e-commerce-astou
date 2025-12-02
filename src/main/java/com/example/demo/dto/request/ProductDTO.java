package com.example.demo.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private int priceCents;
    private int stock;
    private CategoryDTO category;
    private ShopDTO shop; // <--
}
