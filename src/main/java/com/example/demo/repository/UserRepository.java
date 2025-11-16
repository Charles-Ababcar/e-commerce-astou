package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    @Query("SELECT u FROM User u JOIN u.stores s WHERE s.id = :storeId")
    long countByStoreId(@Param("storeId") String storeId);

    Page<User> findByOrderByCreatedAtDesc(Pageable pageable);

    Optional<User> findByUsername(String username);
}
