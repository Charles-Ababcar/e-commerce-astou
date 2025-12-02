package com.example.demo.service;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.OrderItemResponseDTO;
import com.example.demo.dto.request.OrderItemRequest;
import com.example.demo.dto.request.PlaceOrderRequest;
import com.example.demo.model.*;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Récupère toutes les commandes avec pagination
     */
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    /**
     * Récupère une commande par son ID
     */
    public ApiResponse<List<OrderItemResponseDTO>> getOrderById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty()) {
            return new ApiResponse<>("Commande non trouvée avec l'identifiant " + id, null, HttpStatus.NOT_FOUND.value());
        }

        List<OrderItemResponseDTO> itemsDto = order.get().getItems().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new ApiResponse<>("Commande récupérée avec succès", itemsDto, HttpStatus.OK.value());
    }

    /**
     * Passe une commande
     */
    public ApiResponse<List<OrderItemResponseDTO>> placeOrder(PlaceOrderRequest placeOrderRequest) {
        // Vérifie ou crée le client
        Client client = clientRepository.findByEmail(placeOrderRequest.getClient().getEmail())
                .orElseGet(() -> {
                    Client newClient = new Client();
                    newClient.setName(placeOrderRequest.getClient().getName());
                    newClient.setEmail(placeOrderRequest.getClient().getEmail());
                    newClient.setAddress(placeOrderRequest.getClient().getAddress());
                    newClient.setPhoneNumber(placeOrderRequest.getClient().getPhoneNumber());
                    newClient.setCreatedAt(placeOrderRequest.getClient().getCreatedAt());
                    newClient.setUpdatedAt(placeOrderRequest.getClient().getUpdatedAt());
                    return clientRepository.save(newClient);
                });

        Order order = new Order();
        order.setClient(client);
        order.setStatus("PLACED");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        long total = 0;

        // Validation des produits et du stock
        for (OrderItemRequest itemDto : placeOrderRequest.getOrderItems()) {
            Optional<Product> productOpt = productRepository.findById(itemDto.getProductId());
            if (productOpt.isEmpty()) {
                return new ApiResponse<>("Produit non trouvé avec l'identifiant : " + itemDto.getProductId(),
                        null, HttpStatus.BAD_REQUEST.value());
            }
            Product product = productOpt.get();

            if (product.getStock() < itemDto.getQuantity()) {
                return new ApiResponse<>(
                        "Stock insuffisant pour le produit : " + product.getName() +
                                ". Demandé : " + itemDto.getQuantity() + ", Disponible : " + product.getStock(),
                        null,
                        HttpStatus.BAD_REQUEST.value());
            }
        }

        // Création des OrderItems et mise à jour du stock
        for (OrderItemRequest itemDto : placeOrderRequest.getOrderItems()) {
            Product product = productRepository.findById(itemDto.getProductId()).get();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setPriceCents(product.getPriceCents());
            orderItems.add(orderItem);

            total += (long) product.getPriceCents() * itemDto.getQuantity();

            // Met à jour le stock
            product.setStock(product.getStock() - itemDto.getQuantity());
            productRepository.save(product);

            // Assigne la boutique de la commande depuis le produit
            order.setShop(product.getShop());
        }

        order.setTotalCents(total);
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        // Conversion en DTO avec catégorie et image
        List<OrderItemResponseDTO> itemsDto = savedOrder.getItems().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new ApiResponse<>("Commande passée avec succès", itemsDto, HttpStatus.CREATED.value());
    }

    /**
     * Supprime une commande
     */
    public ApiResponse<Void> deleteOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            return new ApiResponse<>("Commande non trouvée avec l'identifiant " + orderId, null, HttpStatus.NOT_FOUND.value());
        }
        orderRepository.deleteById(orderId);
        return new ApiResponse<>("Commande supprimée avec succès", null, HttpStatus.OK.value());
    }

    /**
     * Convertit un OrderItem en DTO avec catégorie et image complète
     */
    private OrderItemResponseDTO convertToDto(OrderItem item) {
        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setPriceCents(item.getPriceCents());

        // Ajouter la catégorie si existante
        if (item.getProduct().getCategory() != null) {
            dto.setCategoryId(item.getProduct().getCategory().getId());
            dto.setCategoryName(item.getProduct().getCategory().getName());
        }

        // Ajouter l'image complète
        if (item.getProduct().getImageUrl() != null) {
            String imageUrl = item.getProduct().getImageUrl().startsWith("http")
                    ? item.getProduct().getImageUrl()
                    : ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(item.getProduct().getImageUrl())
                    .toUriString();
            dto.setImageUrl(imageUrl);
        }

        return dto;
    }
}
