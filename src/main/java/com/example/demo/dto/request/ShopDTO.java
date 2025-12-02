package com.example.demo.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class ShopDTO {
    private Long id;
    private String name;
    private String address;

    public ShopDTO() {
    } // constructeur vide

    public ShopDTO(Long id, String name) {
        this.id = id;
        this.name = name;

    }

}
