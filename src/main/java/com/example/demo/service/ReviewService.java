package com.example.demo.service;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CreateReviewRequest;
import com.example.demo.dto.request.ReviewRequestDTO;
import com.example.demo.dto.request.ReviewResponseDTO;
import com.example.demo.model.Product;
import com.example.demo.model.Review;
import com.example.demo.model.User;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository; // Nécessaire pour mettre à jour le rating

    /**
     * Crée un nouvel avis et recalcule le rating du produit.
     */
    @Transactional
    public ApiResponse<ReviewResponseDTO> createReview(ReviewRequestDTO requestDTO) {
        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        // 1. Création de l'entité Review
        Review review = new Review();
        review.setRating(requestDTO.getRating());
        review.setComment(requestDTO.getComment());
        review.setReviewerName(requestDTO.getReviewerName());
        review.setProduct(product);
        review.setCreatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);

        // 2. Recalcul du Rating Moyen (CRUCIAL)
        recalculateAverageRating(product);

        // 3. Mapping vers DTO
        return new ApiResponse<>("Avis créé avec succès", convertToDto(savedReview), HttpStatus.CREATED.value());
    }

    /**
     * Méthode interne pour recalculer et mettre à jour le rating moyen du produit.
     */
    private void recalculateAverageRating(Product product) {
        List<Review> reviews = reviewRepository.findByProductId(product.getId());

        if (reviews.isEmpty()) {
            product.setRating(0.0);
        } else {
            double newAverage = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0); // Calcule la moyenne des notes

            product.setRating(newAverage);
        }

        productRepository.save(product); // Sauvegarde le produit avec la nouvelle note
    }

    /**
     * Récupère tous les avis pour un produit donné
     */
    public ApiResponse<List<ReviewResponseDTO>> getReviewsByProductId(Long productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        List<ReviewResponseDTO> dtos = reviews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new ApiResponse<>("Avis récupérés", dtos, HttpStatus.OK.value());
    }

    // --- Mappeur DTO ---
    private ReviewResponseDTO convertToDto(Review review) {
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setReviewerName(review.getReviewerName());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setProductId(review.getProduct().getId());
        return dto;
    }
}
