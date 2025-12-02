package com.example.demo.dto.request;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
public class CategoryResponseDTO {
    private Long id;
    private String name;
   private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
