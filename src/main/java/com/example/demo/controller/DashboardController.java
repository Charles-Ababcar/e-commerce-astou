package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.PageResponse;
import com.example.demo.dto.request.*;
import com.example.demo.model.Order;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@PreAuthorize("hasAnyRole('ADMIN' , 'SUPER_ADMIN')")
public class DashboardController {


    @Autowired
    private DashboardService dashboardService;

    // -------------------------
    // Statistiques générales
    // -------------------------
    @GetMapping("/general")
    public ApiResponse<DashboardStatsDTO> getGeneralStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return dashboardService.getGeneralStatistics(startDate, endDate);
    }

    // -------------------------
    // Tendances de ventes
    // -------------------------
    @GetMapping("/sales/trends")
    public ApiResponse<List<SalesTrendDTO>> getSalesTrends(
            @RequestParam String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return dashboardService.getSalesTrends(type, startDate, endDate);
    }

    // -------------------------
    // Produits les plus vendus
    // -------------------------
    @GetMapping("/products/top")
    public ApiResponse<PageResponse<TopProductDTO>> getTopSellingProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return dashboardService.getTopSellingProducts(page, size, startDate, endDate);
    }

    // -------------------------
    // Commandes récentes
    // -------------------------
    @GetMapping("/orders/recent")
    public ApiResponse<PageResponse<RecentOrderDTO>> getRecentOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return dashboardService.getRecentOrders(page, size);
    }

    // -------------------------
    // Clients récents
    // -------------------------
    @GetMapping("/customers/recent")
    public ApiResponse<PageResponse<RecentCustomerDTO>> getRecentCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return dashboardService.getRecentCustomers(page, size);
    }

    // -------------------------
    // Statistiques d'un produit
    // -------------------------
    @GetMapping("/products/{id}/statistics")
    public ApiResponse<Map<String, Object>> getProductStatistics(@PathVariable Long id) {
        return dashboardService.getProductStatistics(id);
    }

    // -------------------------
    // Taux de paniers abandonnés
    // -------------------------
    @GetMapping("/carts/abandoned-rate")
    public ApiResponse<Map<String, Object>> getAbandonedCartsRate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return dashboardService.getAbandonedCartsRate(startDate, endDate);
    }

    // -------------------------
    // Taux de conversion
    // -------------------------
    @GetMapping("/conversion-rate")
    public ApiResponse<Map<String, Object>> getConversionRate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return dashboardService.getConversionRate(startDate, endDate);
    }

    // -------------------------
    // Comparaison des performances
    // -------------------------
    @GetMapping("/performance-comparison")
    public ApiResponse<Map<String, Object>> getPerformanceComparison(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate currentStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate currentEnd,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate previousStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate previousEnd) {
        return dashboardService.getPerformanceComparison(currentStart, currentEnd, previousStart, previousEnd);
    }
}
