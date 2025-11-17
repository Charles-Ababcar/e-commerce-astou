package com.example.demo.service;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.request.PlaceOrderRequest;
import com.example.demo.dto.request.OrderItemRequest;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProductRepository productRepository;

    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public ApiResponse<Order> getOrderById(String id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            return new ApiResponse<>("La commande avec l'identifiant " + id + " a été récupérée avec succès", order.get(), HttpStatus.UNAUTHORIZED.value());
        } else {
            return new ApiResponse<>("Commande non trouvée avec l'identifiant " + id, null, HttpStatus.UNAUTHORIZED.value());
        }
    }

    public ApiResponse<Order> placeOrder(PlaceOrderRequest placeOrderRequest) {
        // Find or create the client
        Client client = clientRepository.findByEmail(placeOrderRequest.getClient().getEmail()).orElseGet(() -> {
            Client newClient = new Client();
            newClient.setName(placeOrderRequest.getClient().getName());
            newClient.setEmail(placeOrderRequest.getClient().getEmail());
            newClient.setAddress(placeOrderRequest.getClient().getAddress());
            return clientRepository.save(newClient);
        });

        Order order = new Order();
        order.setClient(client);
        order.setStatus("PLACED");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        long total = 0;

        // Validate and process order items
        for (OrderItemRequest itemDto : placeOrderRequest.getOrderItems()) {
            Optional<Product> productOpt = productRepository.findById(itemDto.getProductId());
            if (!productOpt.isPresent()) {
                return new ApiResponse<>("Produit non trouvé avec l'identifiant : " + itemDto.getProductId(), null, HttpStatus.UNAUTHORIZED.value());
            }
            Product product = productOpt.get();
            if (product.getStock() < itemDto.getQuantity()) {
                return new ApiResponse<>("Stock insuffisant pour le produit : " + product.getName() + ". Demandé : " + itemDto.getQuantity() + ", Disponible : " + product.getStock(), null, HttpStatus.UNAUTHORIZED.value());
            }
        }

        for (OrderItemRequest itemDto : placeOrderRequest.getOrderItems()) {
            Product product = productRepository.findById(itemDto.getProductId()).get(); // We know it exists

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setPriceCents(product.getPriceCents());
            orderItems.add(orderItem);

            total += (long) product.getPriceCents() * itemDto.getQuantity();
            product.setStock(product.getStock() - itemDto.getQuantity());
            productRepository.save(product);
        }

        order.setTotalCents(total);
        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        return new ApiResponse<>("Commande passée avec succès", savedOrder, HttpStatus.UNAUTHORIZED.value());
    }
}
