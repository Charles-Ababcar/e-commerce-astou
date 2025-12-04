package com.example.demo.repository;

import com.example.demo.model.Order;
import com.example.demo.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {


    // Toutes les commandes triées par date décroissante
    Page<Order> findByOrderByCreatedAtDesc(Pageable pageable);

    // Toutes les commandes entre deux dates
    Page<Order> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Compter les commandes entre deux dates
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Compter toutes les commandes
    long count();

    // Compter les commandes par ID de client
    @Query("SELECT COUNT(o) FROM Order o WHERE o.client.id = :clientId")
    long countByClientId(@Param("clientId") Long clientId);

    // Alternative si votre entité a une relation directe
    // long countByClient_Id(Long clientId); // Avec underscore

    // Trouver les commandes par ID de client
    @Query("SELECT o FROM Order o WHERE o.client.id = :clientId ORDER BY o.createdAt DESC")
    Page<Order> findByClientId(@Param("clientId") Long clientId, Pageable pageable);


    @Query("SELECT p FROM Order p WHERE " +
            "LOWER(p.orderNumber) LIKE LOWER(CONCAT('%', :search, '%')) ")
    Page<Order> searchOrder(@Param("search") String search,
                                 Pageable pageable);


}
