package com.example.demo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCartRequestDTO {

    private Long productId;
    private int quantity;
}
