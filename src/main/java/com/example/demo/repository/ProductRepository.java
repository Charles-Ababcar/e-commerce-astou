package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Compter tous les produits
    long count();

    // Filtrer par catégorie
    long countByCategoryId(Long categoryId);

    Page<Product> findAllByCategoryId(Long categoryId, Pageable pageable);

    // Recherche par nom
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Product> findByShopId(Long shopId, Pageable pageable);


    // ------------------------------------------
    // TOP SELLING PRODUCTS
    // ------------------------------------------
    @Query(value = "SELECT p.* FROM product p " +
            "JOIN order_item oi ON p.id = oi.product_id " +
            "JOIN \"order\" o ON oi.order_id = o.id " +
            "WHERE (:startDate IS NULL OR o.created_at >= :startDate) " +
            "AND (:endDate IS NULL OR o.created_at <= :endDate) " +
            "GROUP BY p.id " +
            "ORDER BY SUM(oi.quantity) DESC",
            countQuery = "SELECT count(*) FROM (SELECT p.id FROM product p " +
                    "JOIN order_item oi ON p.id = oi.product_id " +
                    "JOIN \"order\" o ON oi.order_id = o.id " +
                    "WHERE (:startDate IS NULL OR o.created_at >= :startDate) " +
                    "AND (:endDate IS NULL OR o.created_at <= :endDate) " +
                    "GROUP BY p.id) as top_prod",
            nativeQuery = true)
    Page<Product> findTopSellingProducts(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable);
}
