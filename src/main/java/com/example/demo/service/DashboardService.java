package com.example.demo.service;

import com.example.demo.dto.ApiResponse;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
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

    public ApiResponse<Map<String, Object>> getGeneralStatistics(LocalDate startDate, LocalDate endDate) {
        List<Order> orders = getOrders(startDate, endDate, Pageable.unpaged()).getContent();

        long totalSales = orders.stream().mapToLong(Order::getTotalCents).sum();
        long totalOrders = orders.size();
        long totalCustomers = userRepository.count();
        long totalProducts = productRepository.count();

        Long bestSellingProductId = getBestSellingProductId(orders);
        Product bestSellingProduct = bestSellingProductId != null ? productRepository.findById(bestSellingProductId).orElse(null) : null;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSales", totalSales / 100.0);
        stats.put("totalOrders", totalOrders);
        stats.put("totalCustomers", totalCustomers);
        stats.put("totalProducts", totalProducts);
        stats.put("bestSellingProduct", bestSellingProduct);

        return new ApiResponse<>("Statistiques générales", stats, HttpStatus.OK.value());
    }

    public ApiResponse<Map<String, Object>> getSalesTrends(String type, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();
        result.put("salesTrends", "Données sur les tendances des ventes basées sur les paramètres fournis.");
        return new ApiResponse<>("Tendances des ventes", result, HttpStatus.OK.value());
    }

    public Page<Product> getTopSellingProducts(int page, int size, LocalDate startDate, LocalDate endDate) {
        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        return productRepository.findTopSellingProducts(startDateTime, endDateTime, pageable);
    }

    public Page<Order> getRecentOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findByOrderByCreatedAtDesc(pageable);
    }

    public Page<User> getRecentCustomers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findByOrderByCreatedAtDesc(pageable);
    }

    public ApiResponse<Map<String, Object>> getAbandonedCartsRate(LocalDate startDate, LocalDate endDate) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(1);
        long abandonedCarts = cartRepository.findByUpdatedAtBeforeAndOrderedIsFalse(threshold).size();
        long totalCarts = cartRepository.countByOrderedIsFalse();

        double rate = totalCarts > 0 ? (double) abandonedCarts / totalCarts : 0;

        Map<String, Object> result = new HashMap<>();
        result.put("abandonedCartsRate", rate);
        return new ApiResponse<>("Taux de paniers abandonnés", result, HttpStatus.OK.value());
    }

    public ApiResponse<Map<String, Object>> getConversionRate(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        long totalOrders = (startDateTime != null && endDateTime != null) ?
                orderRepository.countByCreatedAtBetween(startDateTime, endDateTime) : orderRepository.count();

        long totalCarts = (startDateTime != null && endDateTime != null) ?
                cartRepository.countByCreatedAtBetween(startDateTime, endDateTime) : cartRepository.count();

        double rate = totalCarts > 0 ? (double) totalOrders / totalCarts : 0;

        Map<String, Object> result = new HashMap<>();
        result.put("conversionRate", rate);
        return new ApiResponse<>("Taux de conversion", result, HttpStatus.OK.value());
    }

    public ApiResponse<Map<String, Object>> getPerformanceComparison(LocalDate currentStart, LocalDate currentEnd,
                                                                     LocalDate previousStart, LocalDate previousEnd) {
        Map<String, Object> currentPeriodStats = getGeneralStatisticsForPeriod(currentStart, currentEnd);
        Map<String, Object> previousPeriodStats = getGeneralStatisticsForPeriod(previousStart, previousEnd);

        Map<String, Object> comparison = new HashMap<>();
        comparison.put("currentPeriod", currentPeriodStats);
        comparison.put("previousPeriod", previousPeriodStats);

        return new ApiResponse<>("Comparaison des performances", comparison, HttpStatus.OK.value());
    }

    private Page<Order> getOrders(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        if (startDateTime != null && endDateTime != null) {
            return orderRepository.findAllByCreatedAtBetween(startDateTime, endDateTime, pageable);
        } else {
            return orderRepository.findAll(pageable);
        }
    }

    private Long getBestSellingProductId(List<Order> orders) {
        return orders.stream()
                .flatMap(order -> order.getItems().stream())
                .collect(Collectors.groupingBy(item -> item.getProduct().getId(), Collectors.summingLong(OrderItem::getQuantity)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private Map<String, Object> getGeneralStatisticsForPeriod(LocalDate startDate, LocalDate endDate) {
        List<Order> orders = getOrders(startDate, endDate, Pageable.unpaged()).getContent();

        long totalSales = orders.stream().mapToLong(Order::getTotalCents).sum();
        long totalOrders = orders.size();
        long totalCustomers = userRepository.count();
        long totalProducts = productRepository.count();

        Long bestSellingProductId = getBestSellingProductId(orders);
        Product bestSellingProduct = bestSellingProductId != null ? productRepository.findById(bestSellingProductId).orElse(null) : null;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSales", totalSales / 100.0);
        stats.put("totalOrders", totalOrders);
        stats.put("totalCustomers", totalCustomers);
        stats.put("totalProducts", totalProducts);
        stats.put("bestSellingProduct", bestSellingProduct);

        return stats;
    }

    // Dans DashboardService.java
    public ApiResponse<Map<String, Object>> getProductStatistics(Long productId) {
        Product product = productRepository.findById(productId).orElse(null);

        if (product == null) {
            return new ApiResponse<>("Produit non trouvé", null, HttpStatus.NOT_FOUND.value());
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("product", product);

        // Exemple simple : nombre de ventes total pour ce produit
        long totalQuantitySold = orderRepository.findAll().stream()
                .flatMap(order -> order.getItems().stream())
                .filter(item -> item.getProduct().getId().equals(productId))
                .mapToLong(OrderItem::getQuantity)
                .sum();

        stats.put("totalQuantitySold", totalQuantitySold);

        return new ApiResponse<>("Statistiques du produit", stats, HttpStatus.OK.value());
    }

}
