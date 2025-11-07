package com.example.demo.controller;

import com.example.demo.dto.PageResponse;
import com.example.demo.model.Store;
import com.example.demo.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @GetMapping
    public ResponseEntity<PageResponse<Store>> getAllStores(Pageable pageable) {
        Page<Store> stores = storeService.getAllStores(pageable);
        return new ResponseEntity<>(new PageResponse<>(stores), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Store> createStore(@RequestPart("store") Store store, @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            Store createdStore = storeService.createStore(store, imageFile);
            return new ResponseEntity<>(createdStore, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
