package com.example.demo.service.interfaceI;

import com.example.demo.dto.AddItemRequest;
import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.UpdateItemRequest;
import com.example.demo.dto.request.CreateCartRequestDTO;
import com.example.demo.model.Cart;

public interface CartService {

    ApiResponse<Cart> getCartById(Long cartId);

    ApiResponse<Cart> createCart(CreateCartRequestDTO dto);

    ApiResponse<Cart> addItemToCart(Long cartId, AddItemRequest request);

    ApiResponse<Cart> updateCartItem(Long cartId, Long itemId, UpdateItemRequest request);

    ApiResponse<Cart> removeCartItem(Long cartId, Long itemId);
}
