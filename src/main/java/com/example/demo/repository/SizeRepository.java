package com.example.demo.repository;

import com.example.demo.model.Client;
import com.example.demo.model.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SizeRepository extends JpaRepository<Size, Long> {
}
