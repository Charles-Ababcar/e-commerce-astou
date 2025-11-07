package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CreateReviewRequest;
import com.example.demo.dto.PageResponse;
import com.example.demo.model.Review;
import com.example.demo.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public ResponseEntity<PageResponse<Review>> getReviewsByProductId(@RequestParam String productId, Pageable pageable) {
        Page<Review> reviews = reviewService.getReviewsByProductId(productId, pageable);
        return new ResponseEntity<>(new PageResponse<>(reviews), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Review>> createReview(@RequestBody CreateReviewRequest createReviewRequest) {
        ApiResponse<Review> response = reviewService.createReview(createReviewRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
