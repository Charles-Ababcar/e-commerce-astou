package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.request.CategoryRequestDTO;
import com.example.demo.dto.request.CategoryResponseDTO;
import com.example.demo.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {


    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // CREATE
    @PostMapping
    public ApiResponse<CategoryResponseDTO> create(@RequestBody CategoryRequestDTO dto) {
        return new ApiResponse<>("Catégorie créée avec succès",
                categoryService.create(dto),
                HttpStatus.CREATED.value());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ApiResponse<CategoryResponseDTO> update(@PathVariable Long id,
                                                   @RequestBody CategoryRequestDTO dto) {
        return new ApiResponse<>("Catégorie mise à jour",
                categoryService.update(id, dto),
                HttpStatus.OK.value());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return new ApiResponse<>("Catégorie supprimée avec succès", "OK", HttpStatus.OK.value());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ApiResponse<CategoryResponseDTO> getById(@PathVariable Long id) {
        return new ApiResponse<>("Détails de la catégorie",
                categoryService.getById(id),
                HttpStatus.OK.value());
    }

    // LIST ALL
    @GetMapping
    public ApiResponse<List<CategoryResponseDTO>> getAll() {
        return new ApiResponse<>("Liste des catégories",
                categoryService.getAll(),
                HttpStatus.OK.value());
    }
}
