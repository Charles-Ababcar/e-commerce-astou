package com.example.demo.repository;

import com.example.demo.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    // Trouver un client par email
    Optional<Client> findByEmail(String email);

    // Compter les clients créés après une certaine date
    long countByCreatedAtAfter(LocalDateTime date);

    // Méthode alternative si vous voulez une date spécifique
    @Query("SELECT COUNT(c) FROM Client c WHERE c.createdAt >= :date")
    long countClientsAfterDate(@Param("date") LocalDateTime date);
}
