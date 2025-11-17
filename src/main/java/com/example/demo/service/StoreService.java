package com.example.demo.service;

import com.example.demo.model.Store;
import com.example.demo.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    public Page<Store> getAllStores(Pageable pageable) {
        return storeRepository.findAll(pageable);
    }

    public Store createStore(Store store, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            store.setImage(imageFile.getBytes());
        }
        return storeRepository.save(store);
    }
}
