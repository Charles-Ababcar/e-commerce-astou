package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.request.CategoryRequestDTO;
import com.example.demo.dto.request.CategoryResponseDTO;
import com.example.demo.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        return
                categoryService.create(dto);

    }

    // UPDATE
    @PutMapping("/{id}")
    public ApiResponse<CategoryResponseDTO> update(@PathVariable Long id,
                                                   @RequestBody CategoryRequestDTO dto) {
        return categoryService.update(id, dto);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        return categoryService.delete(id);

    }

    // GET BY ID
    @GetMapping("/{id}")
    public ApiResponse<CategoryResponseDTO> getById(@PathVariable Long id) {
        return
                categoryService.getById(id);
    }

    // LIST ALL
    @GetMapping
    public ApiResponse<Page<CategoryResponseDTO>> getAll(  @RequestParam(defaultValue = "") String search,Pageable pageable) {
        return categoryService.getAll(search,pageable);
    }
    @GetMapping("all")
    public ApiResponse<Page<CategoryResponseDTO>> getAllByFrontend(  @RequestParam(defaultValue = "") String search,Pageable pageable) {
        return categoryService.getAll(search,pageable);
    }

}
