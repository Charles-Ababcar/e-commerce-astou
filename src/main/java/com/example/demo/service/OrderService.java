package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.dto.request.OrderItemRequest;
import com.example.demo.dto.request.PlaceOrderRequest;
import com.example.demo.dto.request.ProductResponseDTO;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {


    private OrderRepository orderRepository;


    private ClientRepository clientRepository;


    private ProductRepository productRepository;


    private CartItemRepository cartItemRepository;


    // Dans OrderService.java

    private CartRepository cartRepository;

    private final DeliveryZoneRepository deliveryZoneRepository;
    /**
     * Récupère toutes les commandes avec pagination et mapping DTO
     */


    // Dans src/main/java/com/example/demo/service/OrderService.java

// 🚨 Assurez-vous d'importer l'Enum: import com.example.demo.model.OrderStatus;

    public Page<OrderDTO> getAllOrdersDTO(String search, String status, Pageable pageable) {

        // 1. Détermination des états de filtre (🚨 CETTE PARTIE ÉTAIT MANQUANTE DANS VOTRE FRAGMENT)
        final boolean isSearchActive = search != null && !search.isEmpty();
        final boolean isStatusActive = status != null && !status.equalsIgnoreCase("ALL");

        // 2. Définition du tri
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().isSorted() ? pageable.getSort() : Sort.by(Sort.Direction.DESC, "createdAt")
        );

        // 3. Conversion sécurisée du statut (String vers Enum)
        OrderStatus orderStatus = null;
        if (isStatusActive) {
            try {
                orderStatus = OrderStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Statut de commande invalide reçu: " + status);

            }
        }

        Page<Order> page;

        // 4. Exécuter la requête
        if (isSearchActive || orderStatus != null) {

            String searchParam = isSearchActive ? search : null;


            page = orderRepository.findBySearchAndStatus(searchParam, orderStatus, sortedPageable);

        } else {
            // Aucun filtre actif
            page = orderRepository.findAll(sortedPageable);
        }

        return page.map(this::convertToOrderDto);
    }



    /**
     * Récupère une commande par son ID
     */
    public ApiResponse<OrderDTO> getOrderDtoById(Long id) {
        Optional<Order> orderOpt = orderRepository.findById(id);

        if (orderOpt.isEmpty()) {
            return new ApiResponse<>("Commande non trouvée avec l'identifiant " + id, null, HttpStatus.NOT_FOUND.value());
        }

        Order order = orderOpt.get();

        // Utiliser la méthode de conversion Order -> OrderDTO
        OrderDTO orderDto = convertToOrderDto(order);

        return new ApiResponse<>("Commande récupérée avec succès", orderDto, HttpStatus.OK.value());
    }

    /**
     * Passe une commande
     */
    @Transactional
    public ApiResponse<OrderPlacedResponseDTO> placeOrder(PlaceOrderRequest placeOrderRequest) {

        log.info("🚀 [placeOrder] DÉBUT DU TRAITEMENT");

        // -------------------------
        // 1️⃣ GESTION DU CLIENT
        // -------------------------
        String email = placeOrderRequest.getClient().getEmail();
        Client client;

        if (email != null && !email.trim().isEmpty()) {
            log.info("👤 Vérification par email : {}", email);
            client = clientRepository.findByEmail(email)
                    .orElseGet(() -> createNewClient(placeOrderRequest, email));
        } else {
            log.warn("👤 Email non fourni. Création d'un nouveau client temporaire.");
            client = createNewClient(placeOrderRequest, null);
        }

        // -------------------------
        // 2️⃣ INITIALISATION COMMANDE & LIVRAISON
        // -------------------------
        Order order = new Order();
        order.setClient(client);
        order.setStatus(OrderStatus.PLACED);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // 🚚 GESTION DYNAMIQUE DE LA LIVRAISON
        // On récupère les infos de livraison depuis la zone en base de données
        if (placeOrderRequest.getDeliveryZoneId() == null) {
            return new ApiResponse<>("Zone de livraison obligatoire", null, 400);
        }

        DeliveryZone zone = deliveryZoneRepository.findById(placeOrderRequest.getDeliveryZoneId())
                .orElse(null);

        if (zone == null) {
            log.error("❌ Zone de livraison introuvable ID: {}", placeOrderRequest.getDeliveryZoneId());
            return new ApiResponse<>("Zone de livraison invalide", null, 400);
        }

        // Sécurisation : on utilise le prix de la base de données, pas celui du frontend
        order.setDeliveryFee(zone.getPrice());
        order.setDeliveryZone(zone.getName());
        order.setDeliveryAddressDetail(placeOrderRequest.getDeliveryAddressDetail());

        log.info("🚚 Livraison configurée : {} ({} FCFA)", zone.getName(), zone.getPrice());

        // -------------------------
        // 3️⃣ VÉRIFICATION DU STOCK
        // -------------------------
        for (OrderItemRequest itemDto : placeOrderRequest.getOrderItems()) {
            Product product = productRepository.findById(itemDto.getProductId()).orElse(null);
            if (product == null) {
                return new ApiResponse<>("Produit introuvable", null, 400);
            }
            if (product.getStock() < itemDto.getQuantity()) {
                return new ApiResponse<>("Stock insuffisant pour " + product.getName(), null, 400);
            }
        }

        // -------------------------
        // 4️⃣ CRÉATION DES ITEMS & CALCUL DU TOTAL
        // -------------------------
        List<OrderItem> orderItems = new ArrayList<>();
        long totalProducts = 0;

        for (OrderItemRequest itemDto : placeOrderRequest.getOrderItems()) {
            Product product = productRepository.findById(itemDto.getProductId()).get();

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemDto.getQuantity());
            item.setPriceCents(product.getPriceCents());
            orderItems.add(item);

            totalProducts += (long) product.getPriceCents() * itemDto.getQuantity();

            // Mise à jour stock
            product.setStock(product.getStock() - itemDto.getQuantity());
            productRepository.save(product);

            // On lie la commande au shop du premier produit
            order.setShop(product.getShop());
        }

        // CALCUL FINAL : Produits + Livraison
        long grandTotal = totalProducts + zone.getPrice();
        order.setItems(orderItems);
        order.setTotalCents(grandTotal);

        // -------------------------
        // 5️⃣ SAUVEGARDE & NUMÉRO DE COMMANDE
        // -------------------------
        Order savedOrder = orderRepository.save(order);
        String datePart = savedOrder.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String orderNumber = "CMD-" + datePart + "-" + savedOrder.getId();
        savedOrder.setOrderNumber(orderNumber);
        orderRepository.save(savedOrder);

        // -------------------------
        // 6️⃣ NETTOYAGE DU PANIER
        // -------------------------
        Long cartId = placeOrderRequest.getCartId();
        if (cartId != null) {
            cartRepository.findById(cartId).ifPresent(cart -> {
                try {
                    cartRepository.delete(cart);
                    log.info("✅ Panier supprimé.");
                } catch (Exception e) {
                    log.error("❌ Erreur suppression panier: {}", e.getMessage());
                }
            });
        }

        // -------------------------
        // 7️⃣ PRÉPARATION DE LA RÉPONSE
        // -------------------------
        OrderPlacedResponseDTO responseDto = new OrderPlacedResponseDTO();
        responseDto.setOrderId(savedOrder.getId());
        responseDto.setOrderNumber(savedOrder.getOrderNumber());
        responseDto.setTotalCents(savedOrder.getTotalCents());
        responseDto.setStatus(savedOrder.getStatus().name());
        responseDto.setItems(savedOrder.getItems().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList()));

        log.info("🎉 Commande {} terminée avec succès !", orderNumber);

        return new ApiResponse<>(
                "Commande passée avec succès",
                responseDto,
                HttpStatus.CREATED.value()
        );
    }


    // ⭐️ MÉTHODE D'AIDE À AJOUTER dans OrderService.java pour la création d'un client
    private Client createNewClient(PlaceOrderRequest placeOrderRequest, String email) {
        // Note: placeOrderRequest.getClient() est un DTO contenant les informations
        Client c = new Client();
        c.setName(placeOrderRequest.getClient().getName());
        // Assignation conditionnelle de l'email (peut être NULL)
        c.setEmail(email);
        c.setAddress(placeOrderRequest.getClient().getAddress());
        c.setPhoneNumber(placeOrderRequest.getClient().getPhoneNumber());
        c.setCreatedAt(LocalDateTime.now());
        c.setUpdatedAt(LocalDateTime.now());
        log.info("📌 Nouveau client créé : Nom={}, Email={}", c.getName(), c.getEmail() != null ? c.getEmail() : "NULL");
        return clientRepository.save(c);
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


    @Transactional
    public ApiResponse<OrderDTO> cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID: " + orderId));

        // 1. Validation du statut : ne peut annuler que si la commande n'est pas déjà finalisée
        // 1. Validation du statut : utiliser les méthodes de l'Enum
        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.DELIVERED) {
            return new ApiResponse<>(
                    "Impossible d'annuler. Statut actuel: " + order.getStatus().name(),
                    convertToOrderDto(order),
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        // 2. Mise à jour du Stock (Reversement)
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();

            // ⚠️ Mise à jour du stock : On ajoute la quantité de l'article annulé.
            product.setStock(product.getStock() + item.getQuantity());

            // >> REMARQUE: productRepository.save(product) n'est pas nécessaire ici
            // car la transaction (via @Transactional) prend en charge la persistance des entités modifiées.
        }

        // 3. Mise à jour du statut de la commande
        // C'est cette valeur qui sera utilisée pour exclure la commande des revenus.
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());

        // 4. (Optionnel, si vous avez besoin d'un champ de remboursement)
        // order.setRefundInitiatedAt(LocalDateTime.now());

        Order cancelledOrder = orderRepository.save(order); // Sauvegarde l'Order et met à jour les Products

        // 5. Mapping et Retour DTO
        OrderDTO orderDto = convertToOrderDto(cancelledOrder);

        return new ApiResponse<>(
                "Commande annulée et stock mis à jour avec succès. Prix total marqué pour exclusion des statistiques.",
                orderDto,
                HttpStatus.OK.value()
        );
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

    /**
     * Convertit une entité Order en OrderDTO
     */
    private OrderDTO convertToOrderDto(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setTotalCents(order.getTotalCents());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(order.getStatus().name());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        // À ajouter dans convertToOrderDto
        dto.setDeliveryFee(order.getDeliveryFee());
        dto.setDeliveryZone(order.getDeliveryZone());
        dto.setDeliveryAddressDetail(order.getDeliveryAddressDetail());

        // Mapping des items
        List<OrderItemResponseDTO> itemsDto = order.getItems().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        // 🚨 Correction : Assignation de la liste des items
        dto.setItems(itemsDto);

        // Mapping Client et Shop
        dto.setClient(convertToClientDto(order.getClient()));
        dto.setShop(convertToShopDto(order.getShop()));

        return dto;
    }


    /**
     * Convertit une entité Client en ClientDTO
     */
    private ClientDTO convertToClientDto(Client client) {
        if (client == null) {
            return null;
        }
        return new ClientDTO(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getAddress(),
                client.getPhoneNumber(),
                client.getCreatedAt(),
                client.getUpdatedAt()
        );
    }

    /**
     * Convertit une entité Shop en ShopDTO
     */
    private ShopDTO convertToShopDto(Shop shop) {
        if (shop == null) {
            return null;
        }
        return new ShopDTO(
                shop.getId(),
                shop.getName()
        );
    }

}