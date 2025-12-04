package com.example.demo.service.interfaceI;

import com.example.demo.dto.AddItemRequest;
import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.UpdateItemRequest;
import com.example.demo.dto.request.CartDTO;
import com.example.demo.dto.request.CartsDTO;
import com.example.demo.dto.request.CreateCartRequestDTO;
import com.example.demo.model.Cart;

import java.util.List;

public interface CartService {

    ApiResponse<CartDTO> createCart(CreateCartRequestDTO dto);

    ApiResponse<CartDTO> createEmptyCart();

    ApiResponse<CartDTO> addItemToCart(Long cartId, AddItemRequest request);

    public ApiResponse<CartDTO> getCartById(Long cartId);


    ApiResponse<CartDTO> updateCartItem(Long cartId, Long itemId, UpdateItemRequest request);

    ApiResponse<CartDTO> removeCartItem(Long cartId, Long itemId);

    ApiResponse<CartDTO> clearCart(Long cartId);
}
