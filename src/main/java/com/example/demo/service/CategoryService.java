package com.example.demo.service;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ShopResponseDTO;
import com.example.demo.dto.request.CategoryRequestDTO;
import com.example.demo.dto.request.CategoryResponseDTO;
import com.example.demo.model.Category;
import com.example.demo.model.Shop;
import com.example.demo.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public ApiResponse<CategoryResponseDTO> create(CategoryRequestDTO dto) {

        if (categoryRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Cette catégorie existe déjà.");
        }

        Category category = new Category();
        category.setName(dto.getName());
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        Category saved = categoryRepository.save(category);
        return new ApiResponse<>(
                "Catégorie créée avec succès",
                convertToDto(saved),
                HttpStatus.CREATED.value()
        );


    }

    public ApiResponse<CategoryResponseDTO> update(Long id, CategoryRequestDTO dto) {

        return categoryRepository.findById(id).map(categorie -> {


        categorie.setName(dto.getName());
        categorie.setDescription(dto.getDescription());
        categorie.setUpdatedAt(LocalDateTime.now());

            Category updated = categoryRepository.save(categorie);

            return new ApiResponse<>("Catégorie mise à jour", convertToDto(updated), HttpStatus.OK.value());

        }).orElse(new ApiResponse<>("Catégorie non trouvée", null, HttpStatus.NOT_FOUND.value()));
    }

    public ApiResponse<Void> delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Catégorie non trouvée.");
        }
        categoryRepository.deleteById(id);
        return new ApiResponse<>("Catégorie supprimée", null, HttpStatus.OK.value());

    }


    public ApiResponse<CategoryResponseDTO> getById(Long id) {
        return categoryRepository.findById(id)
                .map(shop -> new ApiResponse<>("Catégorie récupérée", convertToDto(shop), HttpStatus.OK.value()))
                .orElse(new ApiResponse<>("Catégorie non trouvée", null, HttpStatus.NOT_FOUND.value()));
    }



    public ApiResponse<Page<CategoryResponseDTO>> getAll(String search ,Pageable pageable) {

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt") // tri du plus récent au plus ancien
        );
        Page<Category> categoryPage;

        if (search == null || search.trim().isEmpty()) {
            categoryPage = categoryRepository.findAll(sortedPageable);
        } else {
            categoryPage = categoryRepository.searchCategory(search.trim(), sortedPageable);
        }

        Page<CategoryResponseDTO> dtoPage = categoryPage.map(this::convertToDto);
        Page<Category> categoriesPage = categoryRepository.findAll(pageable);
        return new ApiResponse<>("Liste des catégories", dtoPage, HttpStatus.OK.value());
    }


    private CategoryResponseDTO convertToDto(Category category) {
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        return dto;
    }

}
