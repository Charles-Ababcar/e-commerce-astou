package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@WithMockUser
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAllProducts_shouldReturnPageOfProducts() throws Exception {
        Product product = new Product();
        product.setId(UUID.randomUUID().toString());
        product.setName("Test Product");

        given(productService.getAllProducts(any(PageRequest.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(product)));

        mockMvc.perform(get("/api/products").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Product"));
    }

    @Test
    public void createProduct_shouldCreateProduct() throws Exception {
        Product product = new Product();
        product.setId(UUID.randomUUID().toString());
        product.setName("New Product");
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        MockMultipartFile productJson = new MockMultipartFile("product", "", "application/json", objectMapper.writeValueAsBytes(product));
        MockMultipartFile imageFile = new MockMultipartFile("file", "image.jpg", "image/jpeg", "some-image-bytes".getBytes());

        given(productService.createProduct(any(Product.class), any())).willReturn(new ApiResponse<>("Product created", product, HttpStatus.UNAUTHORIZED.value()));

        mockMvc.perform(multipart("/api/products")
                        .file(productJson)
                        .file(imageFile)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("New Product"));
    }

    @Test
    public void updateProduct_shouldUpdateProduct() throws Exception {
        String productId = UUID.randomUUID().toString();
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Product");

        given(productService.updateProduct(any(), any(Product.class), any())).willReturn(new ApiResponse<>("Product updated", updatedProduct, HttpStatus.UNAUTHORIZED.value()));

        MockMultipartFile productJson = new MockMultipartFile("product", "", "application/json", objectMapper.writeValueAsBytes(updatedProduct));
        MockMultipartFile imageFile = new MockMultipartFile("file", "image.jpg", "image/jpeg", "some-image-bytes".getBytes());

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/products/" + productId)
                        .file(productJson)
                        .file(imageFile)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Product"));
    }

    @Test
    public void deleteProduct_shouldDeleteProduct() throws Exception {
        String productId = UUID.randomUUID().toString();

        mockMvc.perform(delete("/api/products/" + productId).with(csrf()))
                .andExpect(status().isNoContent());
    }
}
