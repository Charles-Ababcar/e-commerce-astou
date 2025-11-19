package com.example.demo.service;

import com.example.demo.dto.AddItemRequest;
import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.UpdateItemRequest;
import com.example.demo.dto.request.CreateCartRequestDTO;
import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.model.Product;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.interfaceI.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl  implements CartService {


    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    /**
     * Récupérer un panier
     */
    @Override
    public ApiResponse<Cart> getCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .map(cart -> new ApiResponse<>("Panier récupéré avec succès", cart, HttpStatus.OK.value()))
                .orElseGet(() -> new ApiResponse<>("Panier non trouvé", null, HttpStatus.NOT_FOUND.value()));
    }

    /**
     * Créer un panier + premier produit ajouté
     */
    @Override
    public ApiResponse<Cart> createCart(CreateCartRequestDTO dto) {

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        Cart cart = new Cart();
        cart.setCreatedAt(LocalDateTime.now());
        cart = cartRepository.save(cart);

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(dto.getQuantity());
        cartItemRepository.save(item);

        return new ApiResponse<>(
                "Panier créé et produit ajouté",
                cart,
                HttpStatus.CREATED.value()
        );
    }

    /**
     * Ajouter un produit dans un panier existant
     */
    @Override
    public ApiResponse<Cart> addItemToCart(Long cartId, AddItemRequest request) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Panier non trouvé"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        Optional<CartItem> existing = cartItemRepository.findByCartIdAndProductId(cartId, request.getProductId());

        CartItem cartItem;
        if (existing.isPresent()) {
            cartItem = existing.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
        }

        cartItemRepository.save(cartItem);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return new ApiResponse<>("Produit ajouté au panier", cart, HttpStatus.OK.value());
    }

    /**
     * Modifier la quantité d’un article du panier
     */
    @Override
    public ApiResponse<Cart> updateCartItem(Long cartId, Long itemId, UpdateItemRequest request) {

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Article non trouvé"));

        if (!item.getCart().getId().equals(cartId)) {
            return new ApiResponse<>("Cet article n'appartient pas au panier", null, HttpStatus.BAD_REQUEST.value());
        }

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        Cart cart = item.getCart();
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return new ApiResponse<>("Article mis à jour", cart, HttpStatus.OK.value());
    }

    /**
     * Supprimer un article du panier
     */
    @Override
    public ApiResponse<Cart> removeCartItem(Long cartId, Long itemId) {

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Article non trouvé"));

        if (!item.getCart().getId().equals(cartId)) {
            return new ApiResponse<>("Cet article n'appartient pas au panier", null, HttpStatus.BAD_REQUEST.value());
        }

        cartItemRepository.delete(item);

        Cart cart = cartRepository.findById(cartId).get();
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return new ApiResponse<>("Article supprimé du panier", cart, HttpStatus.OK.value());
    }
}
