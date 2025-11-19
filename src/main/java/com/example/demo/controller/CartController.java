package com.example.demo.controller;

import com.example.demo.dto.AddItemRequest;
import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.UpdateItemRequest;
import com.example.demo.dto.request.CreateCartRequestDTO;
import com.example.demo.model.Cart;

import com.example.demo.service.CartServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartServiceImpl cartService;

    public CartController(CartServiceImpl cartService) {
        this.cartService = cartService;
    }

    /**
     * Récupérer un panier
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Cart>> getCartById(@PathVariable Long id) {
        ApiResponse<Cart> response = cartService.getCartById(id);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Créer un panier vide ou avec un premier produit
     * DTO = seulement productId + quantity
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Cart>> createCart(@RequestBody CreateCartRequestDTO dto) {
        ApiResponse<Cart> response = cartService.createCart(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Ajouter un produit à un panier existant
     */
    @PostMapping("/{cartId}/items")
    public ResponseEntity<ApiResponse<Cart>> addItemToCart(
            @PathVariable Long cartId,
            @RequestBody AddItemRequest request) {

        ApiResponse<Cart> response = cartService.addItemToCart(cartId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Modifier la quantité d’un article du panier
     */
    @PutMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<ApiResponse<Cart>> updateCartItem(
            @PathVariable Long cartId,
            @PathVariable Long itemId,
            @RequestBody UpdateItemRequest request) {

        ApiResponse<Cart> response = cartService.updateCartItem(cartId, itemId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Supprimer un article du panier
     */
    @DeleteMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<ApiResponse<Cart>> removeCartItem(
            @PathVariable Long cartId,
            @PathVariable Long itemId) {

        ApiResponse<Cart> response = cartService.removeCartItem(cartId, itemId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
