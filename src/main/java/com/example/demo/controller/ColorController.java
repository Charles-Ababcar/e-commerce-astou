package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.model.Color;
import com.example.demo.service.AttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colors")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ColorController {
    private final AttributeService attributeService;

    @GetMapping
    public ApiResponse<List<Color>> getAll() {
        List<Color> colors = attributeService.getAllColors();
        return new ApiResponse<>(
                "Couleurs récupérées avec succès",
                colors,
                HttpStatus.OK.value()
        );
    }

    @PostMapping
    public ApiResponse<Color> create(@RequestBody Color color) {
        Color saved = attributeService.createColor(color);
        return new ApiResponse<>(
                "Couleur créée avec succès",
                saved,
                HttpStatus.CREATED.value()
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<Color> updateColor(@PathVariable Long id, @RequestBody Color color) {
        try {
            Color updated = attributeService.updateColor(id, color);
            return new ApiResponse<>(
                    "Couleur mise à jour avec succès",
                    updated,
                    HttpStatus.OK.value()
            );
        } catch (Exception e) {
            return new ApiResponse<>(e.getMessage(), null, HttpStatus.NOT_FOUND.value());
        }
    }
}
