package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.model.Order;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.service.DashboardService;
import com.example.demo.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@WithMockUser
public class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    public void getGeneralStatistics_shouldReturnStats() throws Exception {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenue", 1000.0);
        given(dashboardService.getGeneralStatistics(any(), any(), any()))
                .willReturn(new ApiResponse<>("General stats", stats, HttpStatus.UNAUTHORIZED.value()));

        mockMvc.perform(get("/api/dashboard/general").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalRevenue").value(1000.0));
    }

    @Test
    public void getSalesTrends_shouldReturnTrends() throws Exception {
        Map<String, Object> trends = new HashMap<>();
        trends.put("2023-01-01", 100.0);
        given(dashboardService.getSalesTrends(anyString(), any(), any(), any()))
                .willReturn(new ApiResponse<>("Sales trends", trends, HttpStatus.UNAUTHORIZED.value()));

        mockMvc.perform(get("/api/dashboard/sales/trends").param("type", "daily").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data['2023-01-01']").value(100.0));
    }

    @Test
    public void getTopSellingProducts_shouldReturnProducts() throws Exception {
        Product product = new Product();
        product.setName("Top Product");
        Page<Product> page = new PageImpl<>(Collections.singletonList(product));
        given(dashboardService.getTopSellingProducts(anyInt(), anyInt(), any(), any(), any()))
                .willReturn(page);

        mockMvc.perform(get("/api/dashboard/products/top").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].name").value("Top Product"));
    }

    @Test
    public void getRecentOrders_shouldReturnOrders() throws Exception {
        Order order = new Order();
        order.setId("order1");
        Page<Order> page = new PageImpl<>(Collections.singletonList(order));
        given(dashboardService.getRecentOrders(anyInt(), anyInt(), any()))
                .willReturn(page);

        mockMvc.perform(get("/api/dashboard/orders/recent").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value("order1"));
    }

    @Test
    public void getRecentCustomers_shouldReturnUsers() throws Exception {
        User user = new User();
        user.setUsername("Recent Customer");
        Page<User> page = new PageImpl<>(Collections.singletonList(user));
        given(dashboardService.getRecentCustomers(anyInt(), anyInt()))
                .willReturn(page);

        mockMvc.perform(get("/api/dashboard/customers/recent").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].username").value("Recent Customer"));
    }

    @Test
    public void getProductStatistics_shouldReturnStats() throws Exception {
        Map<String, Object> stats = new HashMap<>();
        stats.put("unitsSold", 50);
        given(dashboardService.getProductStatistics(anyString()))
                .willReturn(new ApiResponse<>("Product stats", stats, HttpStatus.UNAUTHORIZED.value()));

        mockMvc.perform(get("/api/dashboard/products/prod1/statistics").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.unitsSold").value(50));
    }

    @Test
    public void getAbandonedCartsRate_shouldReturnRate() throws Exception {
        Map<String, Object> rate = new HashMap<>();
        rate.put("rate", 0.25);
        given(dashboardService.getAbandonedCartsRate(any(), any(), any()))
                .willReturn(new ApiResponse<>("Abandoned cart rate", rate, HttpStatus.UNAUTHORIZED.value()));

        mockMvc.perform(get("/api/dashboard/carts/abandoned-rate").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.rate").value(0.25));
    }

    @Test
    public void getConversionRate_shouldReturnRate() throws Exception {
        Map<String, Object> rate = new HashMap<>();
        rate.put("rate", 0.05);
        given(dashboardService.getConversionRate(any(), any(), any()))
                .willReturn(new ApiResponse<>("Conversion rate", rate, HttpStatus.UNAUTHORIZED.value()));

        mockMvc.perform(get("/api/dashboard/conversion-rate").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.rate").value(0.05));
    }

    @Test
    public void getRevenueByCategory_shouldReturnRevenue() throws Exception {
        Map<String, Object> revenue = new HashMap<>();
        revenue.put("Electronics", 500.0);
        given(dashboardService.getRevenueByCategory(any(), any(), any()))
                .willReturn(new ApiResponse<>("Revenue by category", revenue, HttpStatus.UNAUTHORIZED.value()));

        mockMvc.perform(get("/api/dashboard/revenue-by-category").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.Electronics").value(500.0));
    }

    @Test
    public void getPerformanceComparison_shouldReturnComparison() throws Exception {
        Map<String, Object> comparison = new HashMap<>();
        comparison.put("revenueChange", 0.1);
        given(dashboardService.getPerformanceComparison(any(LocalDate.class), any(LocalDate.class), any(LocalDate.class), any(LocalDate.class), any()))
                .willReturn(new ApiResponse<>("Performance comparison", comparison, HttpStatus.UNAUTHORIZED.value()));

        mockMvc.perform(get("/api/dashboard/performance-comparison").with(csrf())
                        .param("currentStart", "2023-01-01")
                        .param("currentEnd", "2023-01-31")
                        .param("previousStart", "2022-12-01")
                        .param("previousEnd", "2022-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.revenueChange").value(0.1));
    }
}
