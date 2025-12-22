package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_cents")
    private long totalCents;

    //private String status;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String orderNumber;

    @Column(name = "is_refund_initiated")
    private Boolean isRefundInitiated = false;

    // --- 🚚 NOUVEAUX CHAMPS POUR LA LIVRAISON ---

    @Column(name = "delivery_fee")
    private Long deliveryFee; // Stocke le prix de la zone au moment de l'achat

    @Column(name = "delivery_zone")
    private String deliveryZone; // Nom de la zone (ex: "Zone 1")

    @Column(name = "delivery_address_detail")
    private String deliveryAddressDetail; // Quartier précis (ex: "Almadies")

    // --------------------------------------------

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;  // ajout du shop

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;



    @Enumerated(EnumType.STRING)
    @Column(name = "channel")
    private OrderChannel channel = OrderChannel.WEB;



}
