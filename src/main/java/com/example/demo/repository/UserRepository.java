package com.example.demo.repository;

import com.example.demo.model.Shop;
import com.example.demo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {



    Page<User> findByOrderByCreatedAtDesc(Pageable pageable);

    Optional<User> findByUsername(String username);


    @Query("""
    SELECT s FROM User s\s
    WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%'))
       OR LOWER(s.email) LIKE LOWER(CONCAT('%', :search, '%'))
       OR LOWER(s.username) LIKE LOWER(CONCAT('%', :search, '%'))
       OR LOWER(s.role) LIKE LOWER(CONCAT('%', :search, '%'))
""")
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);


    // Trouver un utilisateur par email
    Optional<User> findByEmail(String email);

    // Trouver les utilisateurs par rôle
    Page<User> findByRole(String role, Pageable pageable);


    // Compter les utilisateurs créés après une certaine date
    long countByCreatedAtAfter(LocalDateTime date);

    // Chercher par nom ou email
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<User> searchByNameOrEmail(@Param("query") String query, Pageable pageable);
}
