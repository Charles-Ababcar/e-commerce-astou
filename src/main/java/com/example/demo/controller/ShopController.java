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

    @PutMapping("/{id}")
    public ApiResponse<ShopResponseDTO> updateShop(
            @PathVariable Long id,
            @ModelAttribute ShopRequestDTO dto,
            @RequestParam(required = false) MultipartFile image) {
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

    @GetMapping("/all-shop")
    public ApiResponse<Page<ShopResponseDTO>> getAllShopsPaginated(Pageable pageable) {
        return shopService.getAllShopsPaginated(pageable);
    }

}
