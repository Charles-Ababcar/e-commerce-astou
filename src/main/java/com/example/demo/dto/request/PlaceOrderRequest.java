package com.example.demo.dto.request;

import com.example.demo.model.Client;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PlaceOrderRequest {

    private Long cartId;
    private Client client;
    private List<OrderItemRequest> orderItems;

    /**
     * L'ID de la zone sélectionnée (Zone 1, Zone 2, etc.)
     * Permet au backend de récupérer le prix officiel (2000, 2500, 3000)
     */
    private Long deliveryZoneId;

    /**
     * Le quartier précis saisi ou sélectionné par le client
     * (ex: "Almadies" ou "Keur Massar")
     */
    private String deliveryAddressDetail;

}
