package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.model.DeliveryZone;
import com.example.demo.service.DeliveryZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivery-zones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DeliveryZoneController {

    private final DeliveryZoneService deliveryZoneService;

    // --- POUR LE CLIENT ET LE DASHBOARD ---
    @GetMapping("/client")
    public ApiResponse<List<DeliveryZone>> getAllActiveZonesClient() {
        List<DeliveryZone> zones = deliveryZoneService.getActiveZones();
        return new ApiResponse<>("Zones de livraison récupérées", zones, HttpStatus.OK.value());
    }
    @GetMapping
    public ApiResponse<List<DeliveryZone>> getAllActiveZones() {
        List<DeliveryZone> zones = deliveryZoneService.getActiveZones();
        return new ApiResponse<>("Zones de livraison récupérées", zones, HttpStatus.OK.value());
    }

    // --- POUR LE DASHBOARD (ADMIN) ---
    @PostMapping
    public ApiResponse<DeliveryZone> createZone(@RequestBody DeliveryZone zone) {
        DeliveryZone savedZone = deliveryZoneService.saveZone(zone);
        return new ApiResponse<>("Zone ajoutée avec succès", savedZone, HttpStatus.CREATED.value());
    }

    @PutMapping("/{id}")
    public ApiResponse<DeliveryZone> updateZone(@PathVariable Long id, @RequestBody DeliveryZone zoneDetails) {
        // Logique simple de mise à jour
        zoneDetails.setId(id);
        DeliveryZone updatedZone = deliveryZoneService.saveZone(zoneDetails);
        return new ApiResponse<>("Zone mise à jour", updatedZone, HttpStatus.OK.value());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteZone(@PathVariable Long id) {
        deliveryZoneService.deleteZone(id);
        return new ApiResponse<>("Zone supprimée", null, HttpStatus.OK.value());
    }
}
