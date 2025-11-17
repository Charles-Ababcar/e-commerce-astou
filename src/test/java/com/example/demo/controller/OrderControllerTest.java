package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.request.OrderItemRequest;
import com.example.demo.dto.request.PlaceOrderRequest;
import com.example.demo.model.Client;
import com.example.demo.model.Order;
import com.example.demo.service.OrderService;
import com.example.demo.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@WithMockUser
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAllOrders_shouldReturnPageOfOrders() throws Exception {
        Order order = new Order();
        order.setId(UUID.randomUUID().toString());

        given(orderService.getAllOrders(any(PageRequest.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(order)));

        mockMvc.perform(get("/api/orders").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(order.getId()));
    }

    @Test
    public void getOrderById_shouldReturnOrder() throws Exception {
        String orderId = UUID.randomUUID().toString();
        Order order = new Order();
        order.setId(orderId);

        given(orderService.getOrderById(anyString())).willReturn(new ApiResponse<>("Order found", order, HttpStatus.UNAUTHORIZED.value()));

        mockMvc.perform(get("/api/orders/" + orderId).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(orderId));
    }

    @Test
    public void placeOrder_shouldCreateOrder() throws Exception {
        PlaceOrderRequest request = new PlaceOrderRequest();
        Client client = new Client();
        client.setId("clientId");
        request.setClient(client);
        OrderItemRequest orderItemRequest = new OrderItemRequest();
        orderItemRequest.setProductId("prodId");
        orderItemRequest.setQuantity(1);
        request.setOrderItems(Collections.singletonList(orderItemRequest));

        Order order = new Order();
        order.setId(UUID.randomUUID().toString());

        given(orderService.placeOrder(any(PlaceOrderRequest.class)))
                .willReturn(new ApiResponse<>("Order placed", order, HttpStatus.UNAUTHORIZED.value()));

        mockMvc.perform(post("/api/orders").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(order.getId()));
    }
}
