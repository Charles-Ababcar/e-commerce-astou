package com.example.demo.service;

import com.example.demo.dto.ApiResponse;
import com.example.demo.model.Order;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    public ApiResponse<Map<String, Object>> getGeneralStatistics(LocalDate startDate, LocalDate endDate, String storeId) {
        List<Order> orders = getOrders(startDate, endDate, storeId, Pageable.unpaged()).getContent();

        long totalSales = orders.stream().mapToLong(Order::getTotalCents).sum();
        long totalOrders = orders.size();
        long totalCustomers = storeId != null ? userRepository.countByStoreId(storeId) : userRepository.count();
        long totalProducts = storeId != null ? productRepository.countByStoreId(storeId) : productRepository.count();

        String bestSellingProductId = getBestSellingProductId(orders);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSales", totalSales / 100.0);
        stats.put("totalOrders", totalOrders);
        stats.put("totalCustomers", totalCustomers);
        stats.put("totalProducts", totalProducts);
        stats.put("bestSellingProduct", productRepository.findById(bestSellingProductId));

        return new ApiResponse<>("Statistiques générales", stats, HttpStatus.UNAUTHORIZED.value());
    }

    public ApiResponse<Map<String, Object>> getSalesTrends(String type, String storeId, LocalDate startDate, LocalDate endDate) {
        // La logique pour les tendances des ventes reste la même
        Map<String, Object> result = new HashMap<>();
        result.put("salesTrends", "Données sur les tendances des ventes basées sur les paramètres fournis.");
        return new ApiResponse<>("Tendances des ventes", result, HttpStatus.UNAUTHORIZED.value());
    }

    public Page<Product> getTopSellingProducts(int page, int size, String storeId, LocalDate startDate, LocalDate endDate) {
        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        return productRepository.findTopSellingProducts(storeId, startDateTime, endDateTime, pageable);
    }

    public Page<Order> getRecentOrders(int page, int size, String storeId) {
        Pageable pageable = PageRequest.of(page, size);
        return storeId != null ?
                orderRepository.findByStoreIdOrderByCreatedAtDesc(storeId, pageable) :
                orderRepository.findByOrderByCreatedAtDesc(pageable);
    }

    public Page<User> getRecentCustomers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findByOrderByCreatedAtDesc(pageable);
    }

    public ApiResponse<Map<String, Object>> getProductStatistics(String id) {
        // La logique pour les statistiques de produits reste la même
        Map<String, Object> result = new HashMap<>();
        result.put("productStats", "Statistics for product with id " + id);
        return new ApiResponse<>("Statistiques du produit", result, HttpStatus.UNAUTHORIZED.value());
    }

    public ApiResponse<Map<String, Object>> getAbandonedCartsRate(String storeId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(1);
        long abandonedCarts;
        if (storeId != null) {
            abandonedCarts = cartRepository.findByStoreIdAndUpdatedAtBeforeAndOrderedIsFalse(storeId, threshold).size();
        } else {
            abandonedCarts = cartRepository.findByUpdatedAtBeforeAndOrderedIsFalse(threshold).size();
        }

        long totalCarts;
        if (storeId != null) {
            totalCarts = cartRepository.countByStoreIdAndOrderedIsFalse(storeId);
        } else {
            totalCarts = cartRepository.countByOrderedIsFalse();
        }

        double rate = totalCarts > 0 ? (double) abandonedCarts / totalCarts : 0;

        Map<String, Object> result = new HashMap<>();
        result.put("abandonedCartsRate", rate);
        return new ApiResponse<>("Taux de paniers abandonnés", result, HttpStatus.UNAUTHORIZED.value());
    }

    public ApiResponse<Map<String, Object>> getConversionRate(String storeId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        long totalOrders;
        long totalCarts;

        if (storeId != null) {
            totalOrders = (startDateTime != null && endDateTime != null) ?
                    orderRepository.countByStoreIdAndCreatedAtBetween(storeId, startDateTime, endDateTime) :
                    orderRepository.countByStoreId(storeId);
            totalCarts = (startDateTime != null && endDateTime != null) ?
                    cartRepository.countByStoreIdAndCreatedAtBetween(storeId, startDateTime, endDateTime) :
                    cartRepository.countByStoreIdAndOrderedIsFalse(storeId);
        } else {
            totalOrders = (startDateTime != null && endDateTime != null) ?
                    orderRepository.countByCreatedAtBetween(startDateTime, endDateTime) :
                    orderRepository.count();
            totalCarts = (startDateTime != null && endDateTime != null) ?
                    cartRepository.countByCreatedAtBetween(startDateTime, endDateTime) :
                    cartRepository.count();
        }

        double rate = totalCarts > 0 ? (double) totalOrders / totalCarts : 0;

        Map<String, Object> result = new HashMap<>();
        result.put("conversionRate", rate);
        return new ApiResponse<>("Taux de conversion", result, HttpStatus.UNAUTHORIZED.value());
    }

    public ApiResponse<Map<String, Object>> getRevenueByCategory(String storeId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        List<Map<String, Object>> revenueByCategory = orderRepository.findRevenueByCategory(storeId, startDateTime, endDateTime);

        Map<String, Object> result = new HashMap<>();
        result.put("revenueByCategory", revenueByCategory);
        return new ApiResponse<>("Revenu par catégorie", result, HttpStatus.UNAUTHORIZED.value());
    }

    public ApiResponse<Map<String, Object>> getPerformanceComparison(LocalDate currentStart, LocalDate currentEnd, LocalDate previousStart, LocalDate previousEnd, String storeId) {
        Map<String, Object> currentPeriodStats = getGeneralStatisticsForPeriod(currentStart, currentEnd, storeId);
        Map<String, Object> previousPeriodStats = getGeneralStatisticsForPeriod(previousStart, previousEnd, storeId);

        Map<String, Object> comparison = new HashMap<>();
        comparison.put("currentPeriod", currentPeriodStats);
        comparison.put("previousPeriod", previousPeriodStats);

        return new ApiResponse<>("Comparaison des performances", comparison, HttpStatus.UNAUTHORIZED.value());
    }

    private Page<Order> getOrders(LocalDate startDate, LocalDate endDate, String storeId, Pageable pageable) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        if (storeId != null) {
            if (startDateTime != null && endDateTime != null) {
                return orderRepository.findAllByStoreIdAndCreatedAtBetween(storeId, startDateTime, endDateTime, pageable);
            } else {
                return orderRepository.findAllByStoreId(storeId, pageable);
            }
        } else {
            if (startDateTime != null && endDateTime != null) {
                return orderRepository.findAllByCreatedAtBetween(startDateTime, endDateTime, pageable);
            } else {
                return orderRepository.findAll(pageable);
            }
        }
    }

    private String getBestSellingProductId(List<Order> orders) {
        return orders.stream()
                .flatMap(order -> order.getItems().stream())
                .collect(Collectors.groupingBy(item -> item.getProduct().getId(), Collectors.summingLong(item -> item.getQuantity())))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private Map<String, Object> getGeneralStatisticsForPeriod(LocalDate startDate, LocalDate endDate, String storeId) {
        List<Order> orders = getOrders(startDate, endDate, storeId, Pageable.unpaged()).getContent();

        long totalSales = orders.stream().mapToLong(Order::getTotalCents).sum();
        long totalOrders = orders.size();
        long totalCustomers = storeId != null ? userRepository.countByStoreId(storeId) : userRepository.count();
        long totalProducts = storeId != null ? productRepository.countByStoreId(storeId) : productRepository.count();

        String bestSellingProductId = getBestSellingProductId(orders);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSales", totalSales / 100.0);
        stats.put("totalOrders", totalOrders);
        stats.put("totalCustomers", totalCustomers);
        stats.put("totalProducts", totalProducts);
        stats.put("bestSellingProduct", productRepository.findById(bestSellingProductId));

        return stats;
    }
}
