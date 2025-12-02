package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Setter
@Getter
public class CartItemDTO {
    private Long id;
    private int quantity;
    private int totalCents;
    private ProductDTO product; // <-- Ajouter ce champ

    public CartItemDTO() {}

    public CartItemDTO(Long id, int quantity, int totalCents, ProductDTO product) {
        this.id = id;
        this.quantity = quantity;
        this.totalCents = totalCents;
        this.product = product;
    }


}
