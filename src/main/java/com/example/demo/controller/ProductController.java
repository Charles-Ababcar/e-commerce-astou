package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.PageResponse;
import com.example.demo.dto.ShopResponseDTO;
import com.example.demo.dto.request.ProductRequestDTO;
import com.example.demo.dto.request.ProductResponseDTO;
import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // CREATE
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductResponseDTO> create(
            @ModelAttribute ProductRequestDTO productRequest,
            @RequestParam(required = false) MultipartFile image) {
        return productService.createProduct(productRequest, image);
    }

    // LIST + SEARCH + PAGINATION
    @GetMapping("/all")
    public ApiResponse<Page<ProductResponseDTO>> getProducts(
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false, defaultValue = "") String search,
            Pageable pageable
    ) {
        Page<ProductResponseDTO> result =
                productService.getAllProducts(shopId, search, pageable);

        return new ApiResponse<>(
                "Liste des produits",
                result,
                HttpStatus.OK.value()
        );
    }

    @GetMapping("/all-frontend")
    public ApiResponse<Page<ProductResponseDTO>> getProductsFrontend(
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false, defaultValue = "") String search,
            Pageable pageable
    ) {
        Page<ProductResponseDTO> result =
                productService.getAllProducts(shopId, search, pageable);

        return new ApiResponse<>(
                "Liste des produits",
                result,
                HttpStatus.OK.value()
        );
    }

    // UPDATE
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductResponseDTO> update(
            @PathVariable Long id,
            @ModelAttribute ProductRequestDTO productRequest,
            @RequestParam(required = false) MultipartFile image) {
        return productService.updateProduct(id, productRequest, image);
    }

    //GET by id
    @GetMapping("/{id}")
    public ApiResponse<ProductResponseDTO> getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("by/{id}")
    public ApiResponse<ProductResponseDTO> getProductFrontendById(@PathVariable Long id) {
        return productService.getProductById(id);
    }
    // DELETE
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }


    /**
     * GET /api/products/shop/{shopId}
     * Récupère tous les produits d'une boutique spécifique avec pagination.
     */
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<PageResponse<ProductResponseDTO>> getProductsByShopId(
            @PathVariable Long shopId,
            Pageable pageable) {

        Page<ProductResponseDTO> productsPage = productService.getProductsByShopId(shopId, pageable);

        // Utilise PageResponse pour encapsuler la page et ses métadonnées
        return ResponseEntity.ok(new PageResponse<>(productsPage));
    }
}
