package com.example.demo.repository;

import com.example.demo.model.Client;
import com.example.demo.model.Color;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColorRepository extends JpaRepository<Color, Long> {
}
