package com.example.demo.repository;

import com.example.demo.model.Category;
import com.example.demo.model.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);

    @Query("""
    SELECT s FROM Category s\s
    WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%'))
       OR LOWER(s.description) LIKE LOWER(CONCAT('%', :search, '%'))
""")
    Page<Category> searchCategory(@Param("search") String search, Pageable pageable);
}