package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // La note donnée par l'utilisateur (ex: 1 à 5)
    @Column(nullable = false)
    private int rating;

    // Le commentaire
    private String comment;

    private String reviewerName; // Nom de l'évaluateur (si non connecté)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Relation Many-to-One avec le Produit
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Vous pouvez ajouter une relation Many-to-One avec l'entité User/Client si les utilisateurs sont connectés.
    /*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    */

}
