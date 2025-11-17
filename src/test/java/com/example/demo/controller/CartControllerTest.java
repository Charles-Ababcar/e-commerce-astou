package com.example.demo.controller;

import com.example.demo.dto.AddItemRequest;
import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.UpdateItemRequest;
import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.model.Product;
import com.example.demo.service.CartService;
import com.example.demo.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@WithMockUser
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getCartById_shouldReturnCart() throws Exception {
        String cartId = UUID.randomUUID().toString();
        Cart cart = new Cart();
        cart.setId(cartId);

        given(cartService.getCartById(anyString())).willReturn(new ApiResponse<>("Cart found", cart, HttpStatus.UNAUTHORIZED.value()));

        mockMvc.perform(get("/api/carts/" + cartId).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(cartId));
    }

    @Test
    public void createCart_shouldCreateCart() throws Exception {
        Cart cart = new Cart();
        cart.setId(UUID.randomUUID().toString());

        given(cartService.createCart(any(Cart.class))).willReturn(new ApiResponse<>("Cart created", cart, HttpStatus.UNAUTHORIZED.value()));

        mockMvc.perform(post("/api/carts").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cart)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(cart.getId()));
    }

    @Test
    public void addItemToCart_shouldAddItem() throws Exception {
        String cartId = UUID.randomUUID().toString();
        AddItemRequest request = new AddItemRequest();
        request.setProductId("productId");
        request.setQuantity(2);

        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setItems(new ArrayList<>());
        CartItem item = new CartItem();
        Product product = new Product();
        product.setId("productId");
        item.setProduct(product);
        item.setQuantity(2);
        cart.getItems().add(item);


        given(cartService.addItemToCart(anyString(), any(AddItemRequest.class)))
                .willReturn(new ApiResponse<>("Item added", cart, HttpStatus.UNAUTHORIZED.value()));

        mockMvc.perform(post("/api/carts/" + cartId + "/items").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].quantity").value(2));
    }

    @Test
    public void updateCartItem_shouldUpdateItem() throws Exception {
        String cartId = UUID.randomUUID().toString();
        String itemId = UUID.randomUUID().toString();
        UpdateItemRequest request = new UpdateItemRequest();
        request.setQuantity(3);
        Cart cart = new Cart();

        given(cartService.updateCartItem(anyString(), anyString(), any(UpdateItemRequest.class)))
                .willReturn(new ApiResponse<>("Item updated", cart, HttpStatus.UNAUTHORIZED.value()));

        mockMvc.perform(put("/api/carts/" + cartId + "/items/" + itemId).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void removeCartItem_shouldRemoveItem() throws Exception {
        String cartId = UUID.randomUUID().toString();
        String itemId = UUID.randomUUID().toString();
        Cart cart = new Cart();

        given(cartService.removeCartItem(anyString(), anyString()))
                .willReturn(new ApiResponse<>("Item removed", cart, HttpStatus.UNAUTHORIZED.value()));

        mockMvc.perform(delete("/api/carts/" + cartId + "/items/" + itemId).with(csrf()))
                .andExpect(status().isOk());
    }
}
