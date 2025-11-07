package com.example.demo.controller;

import com.example.demo.dto.AddItemRequest;
import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.UpdateItemRequest;
import com.example.demo.model.Cart;
import com.example.demo.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Cart>> getCartById(@PathVariable String id) {
        ApiResponse<Cart> response = cartService.getCartById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Cart>> createCart(@RequestBody Cart cart) {
        ApiResponse<Cart> response = cartService.createCart(cart);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<ApiResponse<Cart>> addItemToCart(@PathVariable String cartId, @RequestBody AddItemRequest addItemRequest) {
        ApiResponse<Cart> response = cartService.addItemToCart(cartId, addItemRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<ApiResponse<Cart>> updateCartItem(@PathVariable String cartId, @PathVariable String itemId, @RequestBody UpdateItemRequest updateItemRequest) {
        ApiResponse<Cart> response = cartService.updateCartItem(cartId, itemId, updateItemRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<ApiResponse<Cart>> removeCartItem(@PathVariable String cartId, @PathVariable String itemId) {
        ApiResponse<Cart> response = cartService.removeCartItem(cartId, itemId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
