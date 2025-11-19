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

    // On peut définir le dossier dans application.properties ou y mettre un chemin par défaut
    public ImageUploadService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        // Si chemin relatif, on le transforme en absolu selon le OS
        if (!Paths.get(uploadDir).isAbsolute()) {
            this.root = Paths.get(System.getProperty("user.home")).resolve(uploadDir.trim());
        } else {
            this.root = Paths.get(uploadDir.trim());
        }


        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
                System.out.println("Dossier créé : " + root.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Impossible d'initialiser le dossier pour le téléchargement!", e);
        }
    }

    public String uploadImage(MultipartFile file, String namePrefix) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = namePrefix + "_" + UUID.randomUUID() + extension;

            Path targetLocation = this.root.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation);

            // 🔥 Retourne l’URL complète pour stockage dans la DB
            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(filename)
                    .toUriString();

            return fileUrl;

        } catch (IOException e) {
            throw new RuntimeException("Impossible de stocker le fichier. Erreur : " + e.getMessage(), e);
        }
    }


    public Path getImagePath(String filename) {
        return root.resolve(filename);
    }

    public String getRootPath() {
        return root.toAbsolutePath().toString();
    }


    // récupère l'extension du fichier
    private String getExtension(String originalName) {
        int index = originalName.lastIndexOf('.');
        return index != -1 ? originalName.substring(index) : "";
    }
}

