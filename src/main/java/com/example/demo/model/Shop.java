package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.annotation.Contract;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String description;


    private String address;




    private String imageUrl;

    private String phoneNumber;


    @Column(nullable = false)
    private Boolean isActive = true;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "shop")
    private List<Product> products;




}
