package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CreateReviewRequest;
import com.example.demo.model.Review;
import com.example.demo.service.ReviewService;
import com.example.demo.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@WithMockUser
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getReviewsByProductId_shouldReturnPageOfReviews() throws Exception {
        Review review = new Review();
        review.setComment("Great product!");

        given(reviewService.getReviewsByProductId(anyString(), any(PageRequest.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(review)));

        mockMvc.perform(get("/api/reviews").param("productId", "123").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].comment").value("Great product!"));
    }

    @Test
    public void createReview_shouldCreateReview() throws Exception {
        CreateReviewRequest request = new CreateReviewRequest();
        request.setProductId("productId");
        request.setUserId("userId");
        request.setRating(5);
        request.setComment("Excellent!");

        Review review = new Review();
        review.setComment("Excellent!");

        given(reviewService.createReview(any(CreateReviewRequest.class)))
                .willReturn(new ApiResponse<>("Review created successfully", review, HttpStatus.UNAUTHORIZED.value()));

        mockMvc.perform(post("/api/reviews").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.comment").value("Excellent!"));
    }
}
