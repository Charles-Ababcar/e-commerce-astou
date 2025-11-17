package com.example.demo.service;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CreateReviewRequest;
import com.example.demo.model.Product;
import com.example.demo.model.Review;
import com.example.demo.model.User;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public Page<Review> getReviewsByProductId(String productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable);
    }

    public ApiResponse<Review> createReview(CreateReviewRequest createReviewRequest) {
        Product product = productRepository.findById(createReviewRequest.getProductId()).orElse(null);
        if (product == null) {
            return new ApiResponse<>("Produit introuvable", null, HttpStatus.UNAUTHORIZED.value());
        }

        User user = userRepository.findById(createReviewRequest.getUserId()).orElse(null);
        if (user == null) {
            return new ApiResponse<>("Utilisateur introuvable", null, HttpStatus.UNAUTHORIZED.value());
        }

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(createReviewRequest.getRating());
        review.setComment(createReviewRequest.getComment());

        Review savedReview = reviewRepository.save(review);
        return new ApiResponse<>("Avis créé avec succès", savedReview, HttpStatus.UNAUTHORIZED.value());
    }
}
