package com.example.demo.service;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.request.ProductRequestDTO;
import com.example.demo.dto.request.ProductResponseDTO;
import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.model.Shop;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;
    private final ImageUploadService imageUploadService;

    public Page<ProductResponseDTO> getAllProducts(Long shopId, Pageable pageable) {
        Page<Product> page = (shopId == null)
                ? productRepository.findAll(pageable)
                : productRepository.findByShopId(shopId, pageable);

        return page.map(this::convertToDto);
    }

    public ApiResponse<ProductResponseDTO> createProduct(ProductRequestDTO dto, MultipartFile image) {

        Shop shop = shopRepository.findById(dto.getShopId())
                .orElseThrow(() -> new RuntimeException("Shop introuvable"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPriceCents(dto.getPriceCents());
        product.setStock(dto.getStock());
        product.setShop(shop);
        product.setCategory(category);

        if (image != null && !image.isEmpty()) {
            String imageUrl = imageUploadService.uploadImage(
                    image,
                    dto.getName().replaceAll("\\s+", "_")
            );
            product.setImageUrl(imageUrl);
        }

        Product saved = productRepository.save(product);
        return new ApiResponse<>("Produit créé", convertToDto(saved), HttpStatus.CREATED.value());
    }

    public ApiResponse<ProductResponseDTO> updateProduct(Long id, ProductRequestDTO dto, MultipartFile image) {

        return productRepository.findById(id)
                .map(product -> {
                    product.setName(dto.getName());
                    product.setDescription(dto.getDescription());
                    product.setPriceCents(dto.getPriceCents());
                    product.setStock(dto.getStock());

                    if (dto.getShopId() != null) {
                        Shop shop = shopRepository.findById(dto.getShopId())
                                .orElseThrow(() -> new RuntimeException("Shop introuvable"));
                        product.setShop(shop);
                    }

                    if (dto.getCategoryId() != null) {
                        Category category = categoryRepository.findById(dto.getCategoryId())
                                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));
                        product.setCategory(category);
                    }

                    if (image != null && !image.isEmpty()) {
                        String imageUrl = imageUploadService.uploadImage(
                                image,
                                dto.getName().replaceAll("\\s+", "_")
                        );
                        product.setImageUrl(imageUrl);
                    }

                    Product updated = productRepository.save(product);

                    return new ApiResponse<>("Produit mis à jour", convertToDto(updated), HttpStatus.OK.value());
                })
                .orElse(new ApiResponse<>("Produit introuvable", null, HttpStatus.NOT_FOUND.value()));
    }

    public ApiResponse<Void> deleteProduct(Long id) {
        productRepository.deleteById(id);
        return new ApiResponse<>("Produit supprimé", null, HttpStatus.OK.value());
    }

    private ProductResponseDTO convertToDto(Product p) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setPriceCents(p.getPriceCents());
        dto.setStock(p.getStock());

        if (p.getImageUrl() != null) {
            // URL complète de l'image
            String imageUrl = p.getImageUrl().startsWith("http")
                    ? p.getImageUrl()
                    : ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(p.getImageUrl())
                    .toUriString();
            dto.setImageUrl(imageUrl);
        }

        // Shop
        dto.setShopId(p.getShop().getId());
        dto.setShopName(p.getShop().getName());

        // Category
        if (p.getCategory() != null) {
            dto.setCategoryId(p.getCategory().getId());
            dto.setCategoryName(p.getCategory().getName());
        }

        return dto;
    }
}

