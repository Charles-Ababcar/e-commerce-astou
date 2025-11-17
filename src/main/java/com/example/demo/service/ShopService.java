package com.example.demo.service;

import com.example.demo.model.Shop;
import com.example.demo.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShopService {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    public Page<Shop> getAllShops(Pageable pageable) {
        return shopRepository.findAll(pageable);
    }

    public Shop createShop(Shop shop, MultipartFile imageFile) throws IOException {
        shop.setCreatedAt(LocalDateTime.now());
        shop.setUpdatedAt(LocalDateTime.now());
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = imageUploadService.uploadImage(imageFile);
            shop.setImageUrl(imageUrl);
        }
        return shopRepository.save(shop);
    }

    public Optional<Shop> updateShop(String id, Shop shopDetails, MultipartFile imageFile) throws IOException {
        return shopRepository.findById(id).map(existingShop -> {
            existingShop.setName(shopDetails.getName());
            existingShop.setDescription(shopDetails.getDescription());
            existingShop.setUpdatedAt(LocalDateTime.now());
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = imageUploadService.uploadImage(imageFile);
                existingShop.setImageUrl(imageUrl);
            }
            return shopRepository.save(existingShop);
        });
    }

    public void deleteShop(String id) {
        shopRepository.deleteById(id);
    }
}
