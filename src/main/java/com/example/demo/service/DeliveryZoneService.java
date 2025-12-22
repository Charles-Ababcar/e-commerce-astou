package com.example.demo.service;

import com.example.demo.model.DeliveryZone;
import com.example.demo.repository.DeliveryZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<DeliveryZone> getAllZones(Pageable pageable) {
        return repository.findAll(pageable);
    }

    // Pour l'application Client
    public List<DeliveryZone> getActiveZones() {
        return repository.findByActiveTrue();
    }

    public void deleteZone(Long id) {
        repository.deleteById(id);
    }
}
