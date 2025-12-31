package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.model.Size;
import com.example.demo.service.AttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sizes")
@RequiredArgsConstructor
@CrossOrigin("*")
public class SizeController {
    private final AttributeService attributeService;

    @GetMapping
    public ApiResponse<List<Size>> getAll() {
        List<Size> sizes = attributeService.getAllSizes();
        return new ApiResponse<>(
                "Tailles récupérées avec succès",
                sizes,
                HttpStatus.OK.value()
        );
    }

    @PostMapping
    public ApiResponse<Size> create(@RequestBody Size size) {
        Size saved = attributeService.createSize(size);
        return new ApiResponse<>(
                "Taille créée avec succès",
                saved,
                HttpStatus.CREATED.value()
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<Size> updateSize(@PathVariable Long id, @RequestBody Size size) {
        try {
            Size updated = attributeService.updateSize(id, size);
            return new ApiResponse<>(
                    "Taille mise à jour avec succès",
                    updated,
                    HttpStatus.OK.value()
            );
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), null, HttpStatus.NOT_FOUND.value());
        }
    }
}
