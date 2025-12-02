package com.example.demo.repository;

import com.example.demo.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {


    List<Cart> findByUpdatedAtBeforeAndOrderedIsFalse(LocalDateTime threshold);


    long countByOrderedIsFalse();


    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Cart> findByUserIdIsNull();

}
