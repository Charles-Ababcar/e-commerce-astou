package com.example.demo.controller;

import com.example.demo.dto.PageResponse;
import com.example.demo.model.Shop;
import com.example.demo.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/shops")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @GetMapping
    public ResponseEntity<PageResponse<Shop>> getAllShops(Pageable pageable) {
        Page<Shop> shops = shopService.getAllShops(pageable);
        return new ResponseEntity<>(new PageResponse<>(shops), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Shop> createShop(@RequestPart("shop") Shop shop, @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            Shop createdShop = shopService.createShop(shop, imageFile);
            return new ResponseEntity<>(createdShop, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Shop> updateShop(@PathVariable String id, @RequestPart("shop") Shop shop, @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            return shopService.updateShop(id, shop, imageFile)
                    .map(updatedShop -> new ResponseEntity<>(updatedShop, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShop(@PathVariable String id) {
        shopService.deleteShop(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
