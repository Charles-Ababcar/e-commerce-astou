package com.example.demo.service;

import com.example.demo.model.DeliveryZone;
import com.example.demo.repository.DeliveryZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryZoneService {

    private final DeliveryZoneRepository repository;

    // Pour le Dashboard
    public DeliveryZone saveZone(DeliveryZone zone) {
        return repository.save(zone);
    }

    public List<DeliveryZone> getAllZones() {
        return repository.findAll();
    }

    // Pour l'application Client
    public List<DeliveryZone> getActiveZones() {
        return repository.findByActiveTrue();
    }

    public void deleteZone(Long id) {
        repository.deleteById(id);
    }
}
