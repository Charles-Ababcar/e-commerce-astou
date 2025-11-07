package com.example.demo.controller;

import com.example.demo.model.Shop;
import com.example.demo.service.ShopService;
import com.example.demo.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShopController.class)
@WithMockUser
public class ShopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShopService shopService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAllShops_shouldReturnPageOfShops() throws Exception {
        Shop shop = new Shop();
        shop.setId(UUID.randomUUID().toString());
        shop.setName("Test Shop");

        given(shopService.getAllShops(any(PageRequest.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(shop)));

        mockMvc.perform(get("/api/shops").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Shop"));
    }

    @Test
    public void createShop_shouldCreateShop() throws Exception {
        Shop shop = new Shop();
        shop.setId(UUID.randomUUID().toString());
        shop.setName("New Shop");
        shop.setCreatedAt(LocalDateTime.now());
        shop.setUpdatedAt(LocalDateTime.now());

        MockMultipartFile shopJson = new MockMultipartFile("shop", "", "application/json", objectMapper.writeValueAsBytes(shop));
        MockMultipartFile imageFile = new MockMultipartFile("file", "image.jpg", "image/jpeg", "some-image-bytes".getBytes());

        given(shopService.createShop(any(Shop.class), any())).willReturn(shop);

        mockMvc.perform(multipart("/api/shops")
                        .file(shopJson)
                        .file(imageFile)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Shop"));
    }

    @Test
    public void updateShop_shouldUpdateShop() throws Exception {
        String shopId = UUID.randomUUID().toString();
        Shop updatedShop = new Shop();
        updatedShop.setName("Updated Shop");

        MockMultipartFile shopJson = new MockMultipartFile("shop", "", "application/json", objectMapper.writeValueAsBytes(updatedShop));
        MockMultipartFile imageFile = new MockMultipartFile("file", "image.jpg", "image/jpeg", "some-image-bytes".getBytes());

        given(shopService.updateShop(any(), any(Shop.class), any())).willReturn(Optional.of(updatedShop));

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/shops/" + shopId)
                        .file(shopJson)
                        .file(imageFile)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Shop"));
    }

    @Test
    public void deleteShop_shouldDeleteShop() throws Exception {
        String shopId = UUID.randomUUID().toString();

        mockMvc.perform(delete("/api/shops/" + shopId).with(csrf()))
                .andExpect(status().isNoContent());
    }
}
