package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.model.Client;
import com.example.demo.service.ClientService;
import com.example.demo.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientController.class)
@WithMockUser
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAllClients_shouldReturnPageOfClients() throws Exception {
        Client client = new Client();
        client.setName("Test Client");

        given(clientService.getAllClients(any(PageRequest.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(client)));

        mockMvc.perform(get("/api/clients").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Client"));
    }

    @Test
    public void getClientById_shouldReturnClient() throws Exception {
        Client client = new Client();
        client.setId("123");

        given(clientService.getClientById(anyString())).willReturn(new ApiResponse<>("Client found", client));

        mockMvc.perform(get("/api/clients/123").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("123"));
    }

    @Test
    public void createClient_shouldCreateClient() throws Exception {
        Client client = new Client();
        client.setName("New Client");

        given(clientService.createClient(any(Client.class))).willReturn(new ApiResponse<>("Client created", client));

        mockMvc.perform(post("/api/clients").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("New Client"));
    }

    @Test
    public void deleteClient_shouldDeleteClient() throws Exception {
        given(clientService.deleteClient(anyString())).willReturn(new ApiResponse<>("Client deleted", null));

        mockMvc.perform(delete("/api/clients/123").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
