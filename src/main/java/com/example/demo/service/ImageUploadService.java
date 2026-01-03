package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageUploadService {

    private final Path root;

    public ImageUploadService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        if (!Paths.get(uploadDir).isAbsolute()) {
            this.root = Paths.get(System.getProperty("user.home")).resolve(uploadDir.trim());
        } else {
            this.root = Paths.get(uploadDir.trim());
        }

        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur initialisation dossier uploads", e);
        }
    }

    public String uploadImage(MultipartFile file, String namePrefix) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = getExtension(originalFilename);
            String filename = namePrefix + "_" + UUID.randomUUID() + extension;

            Path targetLocation = this.root.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation);

            // 🔥 ON RETOURNE JUSTE LE NOM DU FICHIER
            // C'est le Service (Product/Shop) qui construira l'URL avec le bon domaine
            return filename;

        } catch (IOException e) {
            throw new RuntimeException("Erreur stockage fichier: " + e.getMessage());
        }
    }

    private String getExtension(String originalName) {
        if (originalName == null) return "";
        int index = originalName.lastIndexOf('.');
        return index != -1 ? originalName.substring(index) : "";
    }
}
