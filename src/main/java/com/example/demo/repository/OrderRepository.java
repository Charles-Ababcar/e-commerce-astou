package com.example.demo.repository;

import com.example.demo.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderRepository extends JpaRepository<Order, String> {

    Page<Order> findByOrderByCreatedAtDesc(Pageable pageable);

    Page<Order> findByStoreIdOrderByCreatedAtDesc(String storeId, Pageable pageable);

    Page<Order> findAllByStoreIdAndCreatedAtBetween(String storeId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Order> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Order> findAllByStoreId(String storeId, Pageable pageable);

    long countByStoreIdAndCreatedAtBetween(String storeId, LocalDateTime start, LocalDateTime end);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByStoreId(String storeId);

    @Query(value = "SELECT c.name, SUM(oi.price_cents * oi.quantity) as revenue " +
            "FROM \"order\" o " +
            "JOIN order_item oi ON o.id = oi.order_id " +
            "JOIN product p ON oi.product_id = p.id " +
            "JOIN category c ON p.category_id = c.id " +
            "WHERE (:storeId IS NULL OR o.store_id = :storeId) " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR o.created_at >= CAST(:startDate AS timestamp)) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR o.created_at <= CAST(:endDate AS timestamp)) " +
            "GROUP BY c.name", nativeQuery = true)
    List<Map<String, Object>> findRevenueByCategory(
            @Param("storeId") String storeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
