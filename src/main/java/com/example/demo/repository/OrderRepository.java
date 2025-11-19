package com.example.demo.repository;

import com.example.demo.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<Order, Long> {


    // Toutes les commandes triées par date décroissante
    Page<Order> findByOrderByCreatedAtDesc(Pageable pageable);

    // Toutes les commandes entre deux dates
    Page<Order> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    // Compter les commandes entre deux dates
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Compter toutes les commandes
    long count();


}
