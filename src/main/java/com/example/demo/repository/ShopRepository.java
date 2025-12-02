package com.example.demo.repository;

import com.example.demo.model.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    @Query("""
    SELECT s FROM Shop s 
    WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%'))
       OR LOWER(s.email) LIKE LOWER(CONCAT('%', :search, '%'))
       OR LOWER(s.description) LIKE LOWER(CONCAT('%', :search, '%'))
       OR LOWER(s.phoneNumber) LIKE LOWER(CONCAT('%', :search, '%'))
       OR LOWER(s.address) LIKE LOWER(CONCAT('%', :search, '%'))
""")
    Page<Shop> searchShops(@Param("search") String search, Pageable pageable);

}
