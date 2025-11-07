package com.example.demo.service;

import com.example.demo.dto.AddItemRequest;
import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.UpdateItemRequest;
import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.model.Product;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public ApiResponse<Cart> getCartById(String id) {
        Cart cart = cartRepository.findById(id).orElse(null);
        if (cart == null) {
            return new ApiResponse<>("Panier non trouvé avec l'identifiant " + id, null);
        }
        return new ApiResponse<>("Le panier avec l'identifiant " + id + " a été récupéré avec succès", cart);
    }

    public ApiResponse<Cart> createCart(Cart cart) {
        Cart createdCart = cartRepository.save(cart);
        return new ApiResponse<>("Panier créé avec succès", createdCart);
    }

    public ApiResponse<Cart> addItemToCart(String cartId, AddItemRequest addItemRequest) {
        return cartRepository.findById(cartId).map(cart -> {
            Product product = productRepository.findById(addItemRequest.getProductId()).orElse(null);
            if (product == null) {
                return new ApiResponse<Cart>("Produit non trouvé avec l'identifiant " + addItemRequest.getProductId(), null);
            }

            Optional<CartItem> existingCartItem = cartItemRepository.findByCartIdAndProductId(cartId, addItemRequest.getProductId());

            if (existingCartItem.isPresent()) {
                CartItem cartItem = existingCartItem.get();
                cartItem.setQuantity(cartItem.getQuantity() + addItemRequest.getQuantity());
                cartItemRepository.save(cartItem);
            } else {
                CartItem newCartItem = new CartItem();
                newCartItem.setCart(cart);
                newCartItem.setProduct(product);
                newCartItem.setQuantity(addItemRequest.getQuantity());
                cartItemRepository.save(newCartItem);
            }

            Cart updatedCart = cartRepository.findById(cartId).get();
            return new ApiResponse<>("Article ajouté au panier", updatedCart);
        }).orElse(new ApiResponse<>("Panier non trouvé avec l'identifiant " + cartId, null));
    }

    public ApiResponse<Cart> updateCartItem(String cartId, String itemId, UpdateItemRequest updateItemRequest) {
        return cartItemRepository.findById(itemId).map(cartItem -> {
            if (!cartItem.getCart().getId().equals(cartId)) {
                return new ApiResponse<Cart>("L'article du panier avec l'identifiant " + itemId + " n'appartient pas au panier avec l'identifiant " + cartId, null);
            }
            cartItem.setQuantity(updateItemRequest.getQuantity());
            cartItemRepository.save(cartItem);
            Cart updatedCart = cartRepository.findById(cartId).get();
            return new ApiResponse<>("Article du panier mis à jour", updatedCart);
        }).orElse(new ApiResponse<>("Article du panier non trouvé avec l'identifiant " + itemId, null));
    }

    public ApiResponse<Cart> removeCartItem(String cartId, String itemId) {
        return cartItemRepository.findById(itemId).map(cartItem -> {
            if (!cartItem.getCart().getId().equals(cartId)) {
                return new ApiResponse<Cart>("L'article du panier avec l'identifiant " + itemId + " n'appartient pas au panier avec l'identifiant " + cartId, null);
            }
            cartItemRepository.delete(cartItem);
            Cart updatedCart = cartRepository.findById(cartId).get();
            return new ApiResponse<>("Article du panier supprimé", updatedCart);
        }).orElse(new ApiResponse<>("Article du panier non trouvé avec l'identifiant " + itemId, null));
    }
}
