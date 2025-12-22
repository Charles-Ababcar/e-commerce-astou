package com.example.demo.repository;

import com.example.demo.model.DeliveryZone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryZoneRepository extends JpaRepository<DeliveryZone, Long> {

    List<DeliveryZone> findByActiveTrue();
}
