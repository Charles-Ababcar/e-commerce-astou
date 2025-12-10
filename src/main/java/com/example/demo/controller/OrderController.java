package com.example.demo.controller;

import com.example.demo.dto.*;
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
    public ResponseEntity<PageResponse<OrderDTO>> getAllOrders(@RequestParam(required = false) String search,
                                                               @RequestParam(required = false) String status,
                                                               Pageable pageable) {

        Page<OrderDTO> ordersPage = orderService.getAllOrdersDTO(search, status, pageable);
        return ResponseEntity.ok(new PageResponse<>(ordersPage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrdersById(@PathVariable Long id) {
        ApiResponse<OrderDTO> response = orderService.getOrderDtoById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Récupère une commande par son ID
     */
    // Dans votre classe OrderController

    @GetMapping("get/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(@PathVariable Long id) {
        ApiResponse<OrderDTO> response = orderService.getOrderDtoById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Place une nouvelle commande
     */
    @PostMapping("/place")
    public ResponseEntity<ApiResponse<OrderPlacedResponseDTO>> placeOrder(
            @RequestBody PlaceOrderRequest placeOrderRequest) {
        ApiResponse<OrderPlacedResponseDTO> response = orderService.placeOrder(placeOrderRequest);
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

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderDTO>> cancelOrder(@PathVariable Long id) {
        ApiResponse<OrderDTO> response = orderService.cancelOrder(id);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
