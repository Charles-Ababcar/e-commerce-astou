package com.example.demo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductRequestDTO {

    private String name;
    private String description;
    private Integer priceCents;
    private Integer stock;
    private Long categoryId;
    private Long shopId;

}
