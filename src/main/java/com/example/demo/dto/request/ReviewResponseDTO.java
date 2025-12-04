package com.example.demo.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewResponseDTO {
    private Long id;
    private int rating;
    private String comment;
    private String reviewerName;
    private LocalDateTime createdAt;
    private Long productId;
}