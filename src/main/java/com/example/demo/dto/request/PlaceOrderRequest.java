package com.example.demo.dto.request;

import com.example.demo.model.Client;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PlaceOrderRequest {

    // Getters and Setters
    private Client client;
    private List<OrderItemRequest> orderItems;

}
