package com.example.demo.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartsDTO {
    private Long id;
    private int totalCents; // total panier
    private List<CartsItemDTO> items;
}
