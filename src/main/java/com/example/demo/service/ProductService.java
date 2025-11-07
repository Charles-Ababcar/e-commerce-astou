package com.example.demo.service;

import com.example.demo.dto.ApiResponse;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;



@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public ApiResponse<Product> getProductById(String id) {
        Product product = productRepository.findById(id).orElse(null);
        return new ApiResponse<>("Le produit avec l'identifiant " + id + " a été récupéré avec succès", product);
    }

    public ApiResponse<Product> createProduct(Product product, MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            String imageUrl = imageUploadService.uploadImage(image);
            product.setImageUrl(imageUrl);
        }
        Product createdProduct = productRepository.save(product);
        return new ApiResponse<>("Produit créé avec succès", createdProduct);
    }

    public ApiResponse<Product> updateProduct(String id, Product productDetails, MultipartFile image) {
        return productRepository.findById(id)
                .map(product -> {
                    if (productDetails.getName() != null) {
                        product.setName(productDetails.getName());
                    }
                    if (productDetails.getDescription() != null) {
                        product.setDescription(productDetails.getDescription());
                    }
                    if (productDetails.getPriceCents() >= 0) {
                        product.setPriceCents(productDetails.getPriceCents());
                    }
                    if (productDetails.getStock() >= 0) {
                        product.setStock(productDetails.getStock());
                    }
                    if (image != null && !image.isEmpty()) {
                        String imageUrl = imageUploadService.uploadImage(image);
                        product.setImageUrl(imageUrl);
                    }
                    Product updatedProduct = productRepository.save(product);
                    return new ApiResponse<>("Produit mis à jour avec succès", updatedProduct);
                })
                .orElse(new ApiResponse<>("Produit non trouvé avec l'identifiant " + id, null));
    }

    public ApiResponse<Void> deleteProduct(String id) {
        productRepository.deleteById(id);
        return new ApiResponse<>("Produit avec l'identifiant " + id + " supprimé avec succès", null);
    }
}
