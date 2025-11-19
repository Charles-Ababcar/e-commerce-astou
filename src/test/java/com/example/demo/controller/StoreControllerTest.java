package com.example.demo.controller;

import com.example.demo.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StoreController.class)
@WithMockUser
public class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreService storeService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAllStores_shouldReturnPageOfStores() throws Exception {
        Store store = new Store();
        store.setId(UUID.randomUUID().toString());
        store.setName("Test Store");

        given(storeService.getAllStores(any(PageRequest.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(store)));

        mockMvc.perform(get("/api/stores").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Store"));
    }

    @Test
    public void createStore_shouldCreateStore() throws Exception {
        Store store = new Store();
        store.setName("New Store");

        MockMultipartFile storeJson = new MockMultipartFile("store", "", "application/json", objectMapper.writeValueAsBytes(store));
        MockMultipartFile imageFile = new MockMultipartFile("file", "image.jpg", "image/jpeg", "some-image-bytes".getBytes());

        given(storeService.createStore(any(Store.class), any())).willReturn(store);

        mockMvc.perform(multipart("/api/stores")
                        .file(storeJson)
                        .file(imageFile)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Store"));
    }
}
