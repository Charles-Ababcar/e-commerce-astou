package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CreateReviewRequest;
import com.example.demo.dto.PageResponse;
import com.example.demo.dto.request.ReviewRequestDTO;
import com.example.demo.dto.request.ReviewResponseDTO;
import com.example.demo.model.Review;
import com.example.demo.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * POST /api/reviews : Crée un nouvel avis (et met à jour le rating du produit)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponseDTO>> createReview(@Valid @RequestBody ReviewRequestDTO requestDTO) {
        ApiResponse<ReviewResponseDTO> response = reviewService.createReview(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * GET /api/reviews/product/{productId} : Récupère tous les avis pour un produit
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<ReviewResponseDTO>>> getReviewsByProductId(@PathVariable Long productId) {
        ApiResponse<List<ReviewResponseDTO>> response = reviewService.getReviewsByProductId(productId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
