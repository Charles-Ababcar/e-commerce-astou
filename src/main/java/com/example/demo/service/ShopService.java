package com.example.demo.service;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ShopResponseDTO;
import com.example.demo.dto.request.ShopRequestDTO;
import com.example.demo.model.Shop;
import com.example.demo.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final ImageUploadService imageUploadService;

    public ApiResponse<ShopResponseDTO> createShop(ShopRequestDTO dto, MultipartFile image) {

        Shop shop = new Shop();
        shop.setName(dto.getName());
        shop.setPhoneNumber(dto.getPhoneNumber());
        shop.setAddress(dto.getAddress());
        shop.setEmail(dto.getEmail());
        shop.setDescription(dto.getDescription());

        // 👉 isActive = true par défaut
        shop.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        shop.setCreatedAt(LocalDateTime.now());
        shop.setUpdatedAt(LocalDateTime.now());

        // 👉 Enregistrer l’image avec URL complète
        if (image != null && !image.isEmpty()) {
            String imageUrl = imageUploadService.uploadImage(
                    image,
                    dto.getName().replaceAll("\\s+", "_")
            );
            shop.setImageUrl(imageUrl);
        }

        Shop saved = shopRepository.save(shop);

        return new ApiResponse<>(
                "Boutique créée avec succès",
                convertToDto(saved),
                HttpStatus.CREATED.value()
        );
    }


    public ApiResponse<ShopResponseDTO> updateShop(Long id, ShopRequestDTO dto, MultipartFile image) {

        return shopRepository.findById(id).map(shop -> {

            shop.setName(dto.getName());

            shop.setPhoneNumber(dto.getPhoneNumber());
            shop.setAddress(dto.getAddress());
            shop.setEmail(dto.getEmail());
            shop.setDescription(dto.getDescription());
            shop.setUpdatedAt(LocalDateTime.now());

            if (image != null && !image.isEmpty()) {
                String imageUrl = imageUploadService.uploadImage(
                        image,
                        dto.getName().replaceAll("\\s+", "_")
                );
                shop.setImageUrl(imageUrl);
            }

            Shop updated = shopRepository.save(shop);

            return new ApiResponse<>("Boutique mise à jour", convertToDto(updated), HttpStatus.OK.value());

        }).orElse(new ApiResponse<>("Boutique non trouvée", null, HttpStatus.NOT_FOUND.value()));
    }

    public ApiResponse<Void> deleteShop(Long id) {
        shopRepository.deleteById(id);
        return new ApiResponse<>("Boutique supprimée", null, HttpStatus.OK.value());
    }

    public ApiResponse<ShopResponseDTO> getShopById(Long id) {
        return shopRepository.findById(id)
                .map(shop -> new ApiResponse<>("Boutique récupérée", convertToDto(shop), HttpStatus.OK.value()))
                .orElse(new ApiResponse<>("Boutique non trouvée", null, HttpStatus.NOT_FOUND.value()));
    }

    public ApiResponse<Page<ShopResponseDTO>> getAllShopsPaginated(Pageable pageable) {
        Page<Shop> shopsPage = shopRepository.findAll(pageable);

        // Convertir chaque Shop en DTO
        Page<ShopResponseDTO> dtoPage = shopsPage.map(this::convertToDto);

        return new ApiResponse<>("Liste des boutiques", dtoPage, HttpStatus.OK.value());
    }

    private ShopResponseDTO convertToDto(Shop shop) {
        ShopResponseDTO dto = new ShopResponseDTO();
        dto.setId(shop.getId());
        dto.setName(shop.getName());
        dto.setDescription(shop.getDescription());
        dto.setPhoneNumber(shop.getPhoneNumber());
        dto.setAddress(shop.getAddress());
        dto.setEmail(shop.getEmail());

        // Génération du lien complet vers l'image
        if (shop.getImageUrl() != null) {

            // Si l'URL commence déjà par http, on ne la modifie pas
            if (shop.getImageUrl().startsWith("http")) {
                dto.setImageUrl(shop.getImageUrl());
            } else {
                // On génère l'URL complète
                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/uploads/")
                        .path(shop.getImageUrl())
                        .toUriString();
                dto.setImageUrl(fileDownloadUri);
            }
        }

        return dto;
    }

}
