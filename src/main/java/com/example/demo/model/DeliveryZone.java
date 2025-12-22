package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "delivery_zones")
@Getter
@Setter
public class DeliveryZone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // ex: "Zone 1"

    @Column(columnDefinition = "TEXT")
    private String areas; // ex: "Almadies, Ngor, Mamelles..."

    @Column(nullable = false)
    private long price; // ex: 2000

    private boolean active = true;
}
