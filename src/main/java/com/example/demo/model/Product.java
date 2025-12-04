package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "price_cents")
    private int priceCents;

    private int stock;

    // ⭐️ AJOUTER LE CHAMP RATING ICI
    @Column(name = "average_rating")
    private Double rating = 0.0; // Initialisé à 0.0 ou null

    @Column(nullable = false)
    private Boolean isActive = true;


    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;


    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;


    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;


}
