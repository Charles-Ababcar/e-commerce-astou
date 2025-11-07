package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ProductRepository extends JpaRepository<Product, String> {

    long countByStoreId(String storeId);

    @Query(value = "SELECT p.* FROM product p " +
            "JOIN order_item oi ON p.id = oi.product_id " +
            "JOIN \"order\" o ON oi.order_id = o.id " +
            "WHERE (:storeId IS NULL OR o.store_id = :storeId) " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR o.created_at >= CAST(:startDate AS timestamp)) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR o.created_at <= CAST(:endDate AS timestamp)) " +
            "GROUP BY p.id " +
            "ORDER BY SUM(oi.quantity) DESC",
            countQuery = "SELECT count(*) FROM (SELECT p.id FROM product p " +
                    "JOIN order_item oi ON p.id = oi.product_id " +
                    "JOIN \"order\" o ON oi.order_id = o.id " +
                    "WHERE (:storeId IS NULL OR o.store_id = :storeId) " +
                    "AND (CAST(:startDate AS timestamp) IS NULL OR o.created_at >= CAST(:startDate AS timestamp)) " +
                    "AND (CAST(:endDate AS timestamp) IS NULL OR o.created_at <= CAST(:endDate AS timestamp)) " +
                    "GROUP BY p.id) as top_products",
            nativeQuery = true)
    Page<Product> findTopSellingProducts(
            @Param("storeId") String storeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
}
