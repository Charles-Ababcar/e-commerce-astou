package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.PageResponse;
import com.example.demo.dto.request.ProductRequestDTO;
import com.example.demo.dto.request.ProductResponseDTO;
import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Object getProducts(
            @RequestParam(required = false) Long shopId,
            Pageable pageable) {
        return productService.getAllProducts(shopId, pageable);
    }

    @PostMapping
    public ApiResponse<ProductResponseDTO> create(
            @ModelAttribute ProductRequestDTO dto,
            @RequestParam(required = false) MultipartFile image) {
        return productService.createProduct(dto, image);
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductResponseDTO> update(
            @PathVariable Long id,
            @ModelAttribute ProductRequestDTO dto,
            @RequestParam(required = false) MultipartFile image) {
        return productService.updateProduct(id, dto, image);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }
}
