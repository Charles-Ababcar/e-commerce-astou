package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.PageResponse;
import com.example.demo.model.Order;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@PreAuthorize("hasAnyRole('ADMIN', 'STORE_OWNER')")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/general")
    public ApiResponse<Map<String, Object>> getGeneralStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String storeId) {
        return dashboardService.getGeneralStatistics(startDate, endDate, storeId);
    }

    @GetMapping("/sales/trends")
    public ApiResponse<Map<String, Object>> getSalesTrends(
            @RequestParam String type,
            @RequestParam(required = false) String storeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return dashboardService.getSalesTrends(type, storeId, startDate, endDate);
    }

    @GetMapping("/products/top")
    public ApiResponse<PageResponse<Product>> getTopSellingProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String storeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Page<Product> topProductsPage = dashboardService.getTopSellingProducts(page, size, storeId, startDate, endDate);
        PageResponse<Product> pageResponse = new PageResponse<>(topProductsPage);
        return new ApiResponse<>("Top produits vendus", pageResponse);
    }

    @GetMapping("/orders/recent")
    public ApiResponse<PageResponse<Order>> getRecentOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String storeId) {
        Page<Order> recentOrdersPage = dashboardService.getRecentOrders(page, size, storeId);
        PageResponse<Order> pageResponse = new PageResponse<>(recentOrdersPage);
        return new ApiResponse<>("Commandes récentes", pageResponse);
    }

    @GetMapping("/customers/recent")
    public ApiResponse<PageResponse<User>> getRecentCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<User> recentCustomersPage = dashboardService.getRecentCustomers(page, size);
        PageResponse<User> pageResponse = new PageResponse<>(recentCustomersPage);
        return new ApiResponse<>("Clients récents", pageResponse);
    }

    @GetMapping("/products/{id}/statistics")
    public ApiResponse<Map<String, Object>> getProductStatistics(@PathVariable String id) {
        return dashboardService.getProductStatistics(id);
    }

    @GetMapping("/carts/abandoned-rate")
    public ApiResponse<Map<String, Object>> getAbandonedCartsRate(
            @RequestParam(required = false) String storeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return dashboardService.getAbandonedCartsRate(storeId, startDate, endDate);
    }

    @GetMapping("/conversion-rate")
    public ApiResponse<Map<String, Object>> getConversionRate(
            @RequestParam(required = false) String storeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return dashboardService.getConversionRate(storeId, startDate, endDate);
    }

    @GetMapping("/revenue-by-category")
    public ApiResponse<Map<String, Object>> getRevenueByCategory(
            @RequestParam(required = false) String storeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return dashboardService.getRevenueByCategory(storeId, startDate, endDate);
    }

    @GetMapping("/performance-comparison")
    public ApiResponse<Map<String, Object>> getPerformanceComparison(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate currentStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate currentEnd,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate previousStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate previousEnd,
            @RequestParam(required = false) String storeId) {
        return dashboardService.getPerformanceComparison(currentStart, currentEnd, previousStart, previousEnd, storeId);
    }
}
