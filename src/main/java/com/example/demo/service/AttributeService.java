package com.example.demo.service;

import com.example.demo.model.Color;
import com.example.demo.model.Size;
import com.example.demo.repository.ColorRepository;
import com.example.demo.repository.SizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttributeService {

    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;

    public List<Color> getAllColors() {
        return colorRepository.findAll();
    }

    public Color createColor(Color color) {
        return colorRepository.save(color);
    }

    public List<Size> getAllSizes() {
        return sizeRepository.findAll();
    }

    public Size createSize(Size size) {
        return sizeRepository.save(size);
    }

    // --- COULEURS ---
    public Color updateColor(Long id, Color colorDetails) {
        return colorRepository.findById(id)
                .map(color -> {
                    color.setName(colorDetails.getName());
                    color.setHexCode(colorDetails.getHexCode());
                    return colorRepository.save(color);
                })
                .orElseThrow(() -> new RuntimeException("Couleur non trouvée avec l'id : " + id));
    }

    // --- TAILLES ---
    public Size updateSize(Long id, Size sizeDetails) {
        return sizeRepository.findById(id)
                .map(size -> {
                    size.setName(sizeDetails.getName());
                    return sizeRepository.save(size);
                })
                .orElseThrow(() -> new RuntimeException("Taille non trouvée avec l'id : " + id));
    }
}
