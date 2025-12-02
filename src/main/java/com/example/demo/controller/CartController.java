package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.dto.request.CartDTO;
import com.example.demo.dto.request.CartsDTO;
import com.example.demo.dto.request.CreateCartRequestDTO;
import com.example.demo.model.Cart;
import com.example.demo.service.CartServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartServiceImpl cartService;

    public CartController(CartServiceImpl cartService) {
        this.cartService = cartService;
    }

    /**
     * Récupérer un panier par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CartDTO>> getCartById(@PathVariable Long id) {
        ApiResponse<CartDTO> response = cartService.getCartById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Récupérer tous les paniers sans utilisateur
     */


    /**
     * Créer un panier avec un premier produit
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CartDTO>> createCart(@RequestBody CreateCartRequestDTO dto) {
        ApiResponse<CartDTO> response = cartService.createCart(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Ajouter un article dans un panier existant
     */
    @PostMapping("/{cartId}/items")
    public ResponseEntity<ApiResponse<CartDTO>> addItemToCart(
            @PathVariable Long cartId,
            @RequestBody AddItemRequest request) {
        ApiResponse<CartDTO> response = cartService.addItemToCart(cartId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Modifier la quantité d’un article dans le panier
     */
    @PutMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<ApiResponse<CartDTO>> updateCartItem(
            @PathVariable Long cartId,
            @PathVariable Long itemId,
            @RequestBody UpdateItemRequest request) {

        ApiResponse<CartDTO> response = cartService.updateCartItem(cartId, itemId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Supprimer un article du panier
     */
    @DeleteMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<ApiResponse<CartDTO>> removeCartItem(
            @PathVariable Long cartId,
            @PathVariable Long itemId) {

        ApiResponse<CartDTO> response = cartService.removeCartItem(cartId, itemId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
