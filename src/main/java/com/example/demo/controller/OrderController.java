package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.OrderItemResponseDTO;
import com.example.demo.dto.PageResponse;
import com.example.demo.dto.request.PlaceOrderRequest;
import com.example.demo.model.Order;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * Récupère toutes les commandes avec pagination
     */



    @GetMapping
    public ResponseEntity<PageResponse<Order>> getAllOrders(Pageable pageable) {
        Page<Order> ordersPage = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(new PageResponse<>(ordersPage));
    }


    /**
     * Récupère une commande par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<List<OrderItemResponseDTO>>> getOrderById(@PathVariable Long id) {
        ApiResponse<List<OrderItemResponseDTO>> response = orderService.getOrderById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Place une nouvelle commande
     */
    @PostMapping("/place")
    public ResponseEntity<ApiResponse<List<OrderItemResponseDTO>>> placeOrder(
            @RequestBody PlaceOrderRequest placeOrderRequest) {
        ApiResponse<List<OrderItemResponseDTO>> response = orderService.placeOrder(placeOrderRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Supprime une commande
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long id) {
        ApiResponse<Void> response = orderService.deleteOrder(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
