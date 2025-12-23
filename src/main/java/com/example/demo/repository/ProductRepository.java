package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Compter tous les produits
    long count();

    // Filtrer par catégorie
    long countByCategoryId(Long categoryId);

    Page<Product> findAllByCategoryId(Long categoryId, Pageable pageable);

    // Recherche par nom
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);



    // ------------------------------------------
    // TOP SELLING PRODUCTS
    // ------------------------------------------
    @Query(value = "SELECT p.* FROM product p " +
            "JOIN order_item oi ON p.id = oi.product_id " +
            "JOIN orders o ON oi.order_id = o.id " +
            "WHERE (CAST(:startDate AS timestamp) IS NULL OR o.created_at >= CAST(:startDate AS timestamp)) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR o.created_at <= CAST(:endDate AS timestamp)) " +
            "GROUP BY p.id " +
            "ORDER BY SUM(oi.quantity) DESC",
            countQuery = "SELECT count(*) FROM (SELECT p.id FROM product p " +
                    "JOIN order_item oi ON p.id = oi.product_id " +
                    "JOIN orders o ON oi.order_id = o.id " +
                    "WHERE (CAST(:startDate AS timestamp) IS NULL OR o.created_at >= CAST(:startDate AS timestamp)) " +
                    "AND (CAST(:endDate AS timestamp) IS NULL OR o.created_at <= CAST(:endDate AS timestamp)) " +
                    "GROUP BY p.id) as top_prod",
            nativeQuery = true)
    Page<Product> findTopSellingProducts(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable);


    @Query("SELECT p FROM Product p WHERE p.shop.id = :shopId AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Product> searchByShopId(@Param("shopId") Long shopId,
                                 @Param("search") String search,
                                 Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Product> searchProducts(@Param("search") String search,
                                 Pageable pageable);

    Page<Product> findByShopId(Long shopId, Pageable pageable);


    Page<Product> findByCategoryId(Long categoryId,Pageable pageable);

}
