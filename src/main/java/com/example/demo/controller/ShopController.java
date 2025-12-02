package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.PageResponse;
import com.example.demo.dto.ShopResponseDTO;
import com.example.demo.dto.request.ShopRequestDTO;
import com.example.demo.model.Shop;
import com.example.demo.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ShopResponseDTO> createShop(
            @ModelAttribute ShopRequestDTO dto,
            @RequestParam(required = false) MultipartFile image) {
        return shopService.createShop(dto, image);
    }

    // Dans votre Contrôleur REST
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // Assurez-vous d'avoir ceci
    public ApiResponse<ShopResponseDTO> updateShop(
            @PathVariable Long id,

            // ✅ CORRECTION MAJEURE: Utilisez @RequestPart pour la partie "shop" du FormData
            // Spring va maintenant chercher et décoder le Blob JSON dans la partie nommée "shop".
            @RequestPart("shop") ShopRequestDTO dto,

            // @RequestPart est également préférable pour les fichiers
            @RequestPart(value = "image", required = false) MultipartFile image) {

        return shopService.updateShop(id, dto, image);
    }



    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteShop(@PathVariable Long id) {
        return shopService.deleteShop(id);
    }

    @GetMapping("/{id}")
    public ApiResponse<ShopResponseDTO> getShopById(@PathVariable Long id) {
        return shopService.getShopById(id);
    }
    @GetMapping("by/{id}")
    public ApiResponse<ShopResponseDTO> getShopFrontendById(@PathVariable Long id) {
        return shopService.getShopById(id);
    }
    @GetMapping("/all-shop")
    public ApiResponse<Page<ShopResponseDTO>> getAllShopsPaginated(Pageable pageable) {
        return shopService.getAllShopsPaginated(pageable);
    }
    // ============================
    // PAGINATION + SEARCH
    // ============================
    @GetMapping("/list")
    public ApiResponse<Page<ShopResponseDTO>> getAllShopsPaginated(
            @RequestParam(defaultValue = "") String search,
            Pageable pageable
    ) {
        return shopService.getAllShopsPaginated(search, pageable);
    }
    @GetMapping("/list-frontend")
    public ApiResponse<Page<ShopResponseDTO>> getAllShops(
            @RequestParam(defaultValue = "") String search,
            Pageable pageable
    ) {
        return shopService.getAllShopsPaginated(search, pageable);
    }

}
