package com.example.demo.service;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ShopResponseDTO;
import com.example.demo.dto.request.CategoryResponseDTO;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;
    private final ImageUploadService imageUploadService;

    public Page<ProductResponseDTO> getAllProducts(Long shopId, String search, Pageable pageable) {

        // Ajouter tri DESC par createdAt au pageable
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt") // tri du plus récent au plus ancien
        );

        Page<Product> page;

        if (shopId != null && search != null && !search.isEmpty()) {
            page = productRepository.searchByShopId(shopId, search, sortedPageable);

        } else if (shopId != null) {
            page = productRepository.findByShopId(shopId, sortedPageable);

        } else if (search != null && !search.isEmpty()) {
            page = productRepository.searchProducts(search, sortedPageable);

        } else {
            page = productRepository.findAll(sortedPageable);
        }

        return page.map(this::convertToDto);
    }


    public ApiResponse<ProductResponseDTO> createProduct(ProductRequestDTO dto, MultipartFile image) {
        try {
            // Validation des champs obligatoires
            validateProductRequest(dto, image);

            Product product = new Product();
            product.setName(dto.getName().trim());
            product.setDescription(dto.getDescription());
            product.setPriceCents(dto.getPriceCents());
            product.setStock(dto.getStock());


            // 👉 isActive = true par défaut
            product.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

            // Gestion optionnelle des relations
            if (dto.getShopId() != null) {
                Shop shop = shopRepository.findById(dto.getShopId())
                        .orElseThrow(() -> new RuntimeException("Boutique avec ID " + dto.getShopId() + " introuvable"));
                product.setShop(shop);
            }

            if (dto.getCategoryId() != null) {
                Category category = categoryRepository.findById(dto.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Catégorie avec ID " + dto.getCategoryId() + " introuvable"));
                product.setCategory(category);
            }

            LocalDateTime now = LocalDateTime.now();
            product.setCreatedAt(now);
            product.setUpdatedAt(now);

            // Upload de l'image
            if (image != null && !image.isEmpty()) {
                String cleanName = dto.getName().replaceAll("[^a-zA-Z0-9]", "_");
                String imageUrl = imageUploadService.uploadImage(image, cleanName);
                product.setImageUrl(imageUrl);
            }

            Product saved = productRepository.save(product);

            return new ApiResponse<>(
                    "Produit créé avec succès",
                    convertToDto(saved),
                    HttpStatus.CREATED.value()
            );

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création du produit: " + e.getMessage(), e);
        }
    }

    private void validateProductRequest(ProductRequestDTO dto, MultipartFile image) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new RuntimeException("Le nom du produit est obligatoire");
        }

        if (dto.getPriceCents() == null) {
            throw new RuntimeException("Le prix est obligatoire");
        }

        if (dto.getPriceCents() < 0) {
            throw new RuntimeException("Le prix ne peut pas être négatif");
        }

        if (dto.getStock() == null) {
            throw new RuntimeException("Le stock est obligatoire");
        }

        if (dto.getStock() < 0) {
            throw new RuntimeException("Le stock ne peut pas être négatif");
        }

        // Pour la création, l'image est obligatoire
        if (image == null || image.isEmpty()) {
            throw new RuntimeException("L'image du produit est obligatoire");
        }
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
                        String cleanName = dto.getName().replaceAll("[^a-zA-Z0-9]", "_");
                        String imageUrl = imageUploadService.uploadImage(image, cleanName);
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

    public ApiResponse<ProductResponseDTO> getProductById(Long id) {
        return productRepository.findById(id)
                .map(product -> new ApiResponse<>("Produit récupérée", convertToDto(product), HttpStatus.OK.value()))
                .orElse(new ApiResponse<>("Produit non trouvée", null, HttpStatus.NOT_FOUND.value()));
    }



    // Dans ProductService (ou un utilitaire de conversion)

    private ShopResponseDTO convertToShopDto(Shop shop) {
        // Créez ici la logique pour convertir l'entité Shop en ShopResponseDTO
        ShopResponseDTO dto = new ShopResponseDTO();
        dto.setId(shop.getId());
        dto.setName(shop.getName());
        dto.setEmail(shop.getEmail());
        dto.setAddress(shop.getAddress());
        dto.setPhoneNumber(shop.getPhoneNumber());
        dto.setDescription(shop.getDescription());
        dto.setCreatedAt(shop.getCreatedAt());
        dto.setUpdatedAt(shop.getUpdatedAt());
        return dto;
    }

    private CategoryResponseDTO convertToCategoryDto(Category category) {

        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setUpdatedAt(category.getUpdatedAt());
        return dto;
    }
    private ProductResponseDTO convertToDto(Product p) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setPriceCents(p.getPriceCents());
        dto.setStock(p.getStock());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setUpdatedAt(p.getUpdatedAt());

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

        // Shop : Conversion et affectation du DTO imbriqué
        // Note : Supposons que p.getShop() n'est jamais nul pour un produit valide.
        dto.setCShopResponseDTO(convertToShopDto(p.getShop()));


        // Category : Conversion et affectation conditionnelle du DTO imbriqué
        if (p.getCategory() != null) {
            dto.setCategoryResponseDTO(convertToCategoryDto(p.getCategory()));
        }

        return dto;
    }
}

