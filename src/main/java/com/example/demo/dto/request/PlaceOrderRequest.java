package com.example.demo.dto.request;

import com.example.demo.model.Client;

import java.util.List;

public class PlaceOrderRequest {

    private Client client;
    private List<OrderItemRequest> orderItems;

    // Getters and Setters
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<OrderItemRequest> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemRequest> orderItems) {
        this.orderItems = orderItems;
    }
}
