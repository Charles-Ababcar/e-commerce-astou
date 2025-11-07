package com.example.demo.repository;

import com.example.demo.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CartRepository extends JpaRepository<Cart, String> {

    List<Cart> findByStoreIdAndUpdatedAtBeforeAndOrderedIsFalse(String storeId, LocalDateTime threshold);

    List<Cart> findByUpdatedAtBeforeAndOrderedIsFalse(LocalDateTime threshold);

    long countByStoreIdAndOrderedIsFalse(String storeId);

    long countByOrderedIsFalse();

    long countByStoreIdAndCreatedAtBetween(String storeId, LocalDateTime startDate, LocalDateTime endDate);

    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

}
