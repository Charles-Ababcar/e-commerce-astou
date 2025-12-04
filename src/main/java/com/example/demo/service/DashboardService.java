// DashboardService.java
package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.dto.request.*;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
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

    @Autowired
    private ClientRepository clientRepository;

    public ApiResponse<DashboardStatsDTO> getGeneralStatistics(LocalDate startDate, LocalDate endDate) {
        List<Order> orders = getOrders(startDate, endDate, Pageable.unpaged()).getContent();

        // Calcul des statistiques en FCFA (pas de division par 100)
        long totalRevenue = orders.stream()
                .mapToLong(Order::getTotalCents) // Supposons que totalCents est en FCFA
                .sum();
        BigDecimal totalRevenueFCFA = BigDecimal.valueOf(totalRevenue);

        long totalOrders = orders.size();
        long totalCustomers = clientRepository.count();
        long totalProducts = productRepository.count();

        // Clients des 30 derniers jours
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        long newCustomers = clientRepository.countByCreatedAtAfter(
                thirtyDaysAgo.atStartOfDay()
        );

        // Taux de conversion (simplifié)
        long totalCarts = cartRepository.count();
        double conversionRate = totalCarts > 0 ?
                ((double) totalOrders / totalCarts) * 100 : 0;

        // Panier moyen en FCFA
        BigDecimal averageOrderValue = totalOrders > 0 ?
                totalRevenueFCFA.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO;

        // Tendences (simulées pour l'exemple)
        Map<String, Double> trends = new HashMap<>();
        trends.put("revenueGrowth", 12.5);
        trends.put("orderGrowth", 8.2);
        trends.put("customerGrowth", 15.3);
        trends.put("conversionGrowth", 4.7);

        // Construction du DTO
        DashboardStatsDTO stats = new DashboardStatsDTO();
        stats.setTotalRevenue(totalRevenueFCFA);
        stats.setTotalOrders(totalOrders);
        stats.setTotalCustomers(totalCustomers);
        stats.setTotalProducts(totalProducts);
        stats.setNewCustomers(newCustomers);
        stats.setConversionRate(conversionRate);
        stats.setAverageOrderValue(averageOrderValue);
        stats.setTrends(trends);

        return new ApiResponse<>("Statistiques générales", stats, HttpStatus.OK.value());
    }


    public ApiResponse<List<SalesTrendDTO>> getSalesTrends(String type, LocalDate startDate, LocalDate endDate) {
        List<SalesTrendDTO> trends = new ArrayList<>();

        // Dates par défaut si non spécifiées
        LocalDate currentDate = startDate != null ? startDate : LocalDate.now().minusDays(30);
        LocalDate finalEndDate = endDate != null ? endDate : LocalDate.now();

        Random random = new Random();

        // Génération de données de test selon le type
        while (!currentDate.isAfter(finalEndDate)) {
            SalesTrendDTO trend = new SalesTrendDTO();
            trend.setDate(currentDate);

            // Données aléatoires réalistes
            int baseRevenue = 500 + random.nextInt(1000);
            int baseSales = 10 + random.nextInt(50);
            int baseCustomers = 5 + random.nextInt(20);

            // Variation selon le type
            switch (type) {
                case "daily":
                    trend.setRevenue(BigDecimal.valueOf(baseRevenue));
                    trend.setSales(baseSales);
                    trend.setCustomers(baseCustomers);
                    currentDate = currentDate.plusDays(1);
                    break;
                case "weekly":
                    trend.setRevenue(BigDecimal.valueOf(baseRevenue * 7));
                    trend.setSales(baseSales * 7);
                    trend.setCustomers(baseCustomers * 7);
                    currentDate = currentDate.plusWeeks(1);
                    break;
                case "monthly":
                    trend.setRevenue(BigDecimal.valueOf(baseRevenue * 30));
                    trend.setSales(baseSales * 30);
                    trend.setCustomers(baseCustomers * 30);
                    currentDate = currentDate.plusMonths(1);
                    break;
                default:
                    currentDate = currentDate.plusDays(1);
            }

            trends.add(trend);
        }

        return new ApiResponse<>("Tendances des ventes", trends, HttpStatus.OK.value());
    }

//    public ApiResponse<PageResponse<TopProductDTO>> getTopSellingProducts(int page, int size, LocalDate startDate, LocalDate endDate) {
//        Pageable pageable = PageRequest.of(page, size);
//        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
//        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
//
//        Page<Product> productPage = productRepository.findTopSellingProducts(startDateTime, endDateTime, pageable);
//
//        // Conversion en DTO
//        List<TopProductDTO> topProductDTOs = productPage.getContent().stream()
//                .map(product -> {
//                    TopProductDTO dto = new TopProductDTO();
//                    dto.setProductId(product.getId());
//                    dto.setProductName(product.getName());
//                    dto.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : "Non catégorisé");
//                    dto.setImageUrl(product.getImageUrl());
//
//                    // Calcul des ventes (simplifié)
//                    Long totalSold = orderRepository.findAll().stream()
//                            .flatMap(order -> order.getItems().stream())
//                            .filter(item -> item.getProduct().getId().equals(product.getId()))
//                            .mapToLong(OrderItem::getQuantity)
//                            .sum();
//
//                    dto.setTotalSold(totalSold);
//                    // Montant total en FCFA (pas de division par 100)
//                    dto.setTotalRevenue(BigDecimal.valueOf(totalSold * product.getPriceCents()));
//
//                    return dto;
//                })
//                .sorted((a, b) -> b.getTotalSold().compareTo(a.getTotalSold()))
//                .collect(Collectors.toList());
//
//        // Création de la page avec DTOs
//        Page<TopProductDTO> dtoPage = new org.springframework.data.domain.PageImpl<>(
//                topProductDTOs, pageable, productPage.getTotalElements()
//        );
//
//        return new ApiResponse<>(
//                "Top produits vendus",
//                new PageResponse<>(dtoPage),
//                HttpStatus.OK.value()
//        );
//    }

    public ApiResponse<PageResponse<TopProductDTO>> getTopSellingProducts(int page, int size, LocalDate startDate, LocalDate endDate) {
        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        // Récupérer TOUS les produits pour calculer les statistiques
        Page<Product> productPage = productRepository.findAll(pageable);

        // Calculer les ventes pour chaque produit
        List<TopProductDTO> topProductDTOs = productPage.getContent().stream()
                .map(product -> {
                    TopProductDTO dto = new TopProductDTO();
                    dto.setProductId(product.getId());
                    dto.setProductName(product.getName());
                    dto.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : "Non catégorisé");
                    dto.setImageUrl(product.getImageUrl());

                    // Calculer les ventes réelles pour ce produit
                    long totalSold = 0;
                    BigDecimal totalRevenue = BigDecimal.ZERO;

                    // Récupérer toutes les commandes dans la période
                    List<Order> relevantOrders;
                    if (startDateTime != null && endDateTime != null) {
                        relevantOrders = orderRepository.findByCreatedAtBetween(startDateTime, endDateTime);
                    } else {
                        relevantOrders = orderRepository.findAll();
                    }

                    // Calculer les ventes pour ce produit
                    for (Order order : relevantOrders) {
                        for (OrderItem item : order.getItems()) {
                            if (item.getProduct().getId().equals(product.getId())) {
                                totalSold += item.getQuantity();
                                totalRevenue = totalRevenue.add(
                                        BigDecimal.valueOf(item.getQuantity() * product.getPriceCents())
                                );
                            }
                        }
                    }

                    dto.setTotalSold(totalSold);
                    dto.setTotalRevenue(totalRevenue);

                    return dto;
                })
                .filter(dto -> dto.getTotalSold() > 0) // Filtrer seulement les produits vendus
                .sorted((a, b) -> b.getTotalSold().compareTo(a.getTotalSold())) // Trier par ventes décroissantes
                .collect(Collectors.toList());

        // Si aucun produit vendu, retourner une liste vide
        if (topProductDTOs.isEmpty()) {
            Page<TopProductDTO> emptyPage = new org.springframework.data.domain.PageImpl<>(
                    Collections.emptyList(), pageable, 0
            );
            return new ApiResponse<>(
                    "Top produits vendus",
                    new PageResponse<>(emptyPage),
                    HttpStatus.OK.value()
            );
        }

        // Créer la page paginée
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), topProductDTOs.size());

        List<TopProductDTO> paginatedList = topProductDTOs.subList(start, end);

        Page<TopProductDTO> dtoPage = new org.springframework.data.domain.PageImpl<>(
                paginatedList, pageable, topProductDTOs.size()
        );

        return new ApiResponse<>(
                "Top produits vendus",
                new PageResponse<>(dtoPage),
                HttpStatus.OK.value()
        );
    }


//    public ApiResponse<PageResponse<RecentOrderDTO>> getRecentOrders(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Order> orderPage = orderRepository.findByOrderByCreatedAtDesc(pageable);
//
//        // Conversion en DTO
//        List<RecentOrderDTO> recentOrderDTOs = orderPage.getContent().stream()
//                .map(order -> {
//                    RecentOrderDTO dto = new RecentOrderDTO();
//                    dto.setOrderId(order.getId());
//                    dto.setOrderNumber("CMD-" + order.getId());
//                    // Montant en FCFA directement
//                    dto.setTotal(BigDecimal.valueOf(order.getTotalCents()));
//                    dto.setStatus(order.getStatus());
//                    dto.setCreatedAt(order.getCreatedAt().toLocalDate().atStartOfDay());
//                    dto.setCustomerName(order.getClient().getName());
//                    dto.setCustomerEmail(order.getClient().getEmail());
//                    dto.setShopName(order.getShop() != null ? order.getShop().getName() : "Boutique inconnue");
//                    return dto;
//                })
//                .collect(Collectors.toList());
//
//        Page<RecentOrderDTO> dtoPage = new org.springframework.data.domain.PageImpl<>(
//                recentOrderDTOs, pageable, orderPage.getTotalElements()
//        );
//
//        return new ApiResponse<>(
//                "Commandes récentes",
//                new PageResponse<>(dtoPage),
//                HttpStatus.OK.value()
//        );
//    }


    public ApiResponse<PageResponse<RecentOrderDTO>> getRecentOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage = orderRepository.findByOrderByCreatedAtDesc(pageable);

        // Conversion en DTO
        List<RecentOrderDTO> recentOrderDTOs = orderPage.getContent().stream()
                .map(order -> {
                    RecentOrderDTO dto = new RecentOrderDTO();
                    dto.setOrderId(order.getId());
                    dto.setOrderNumber(order.getOrderNumber() != null ? order.getOrderNumber() : "CMD-" + order.getId());
                    // Montant en FCFA directement
                    dto.setTotal(BigDecimal.valueOf(order.getTotalCents()));
                    dto.setStatus(order.getStatus() != null ? order.getStatus().toString() : "PLACED");
                    dto.setCreatedAt(order.getCreatedAt());
                    dto.setCustomerName(order.getClient() != null ? order.getClient().getName() : "Client inconnu");
                    dto.setCustomerEmail(order.getClient() != null ? order.getClient().getEmail() : "");
                    dto.setShopName(order.getShop() != null ? order.getShop().getName() : "Boutique en ligne");
                    return dto;
                })
                .collect(Collectors.toList());

        Page<RecentOrderDTO> dtoPage = new org.springframework.data.domain.PageImpl<>(
                recentOrderDTOs, pageable, orderPage.getTotalElements()
        );

        return new ApiResponse<>(
                "Commandes récentes",
                new PageResponse<>(dtoPage),
                HttpStatus.OK.value()
        );
    }

    public ApiResponse<PageResponse<RecentCustomerDTO>> getRecentCustomers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findByOrderByCreatedAtDesc(pageable);

        // Conversion en DTO
        List<RecentCustomerDTO> recentCustomerDTOs = userPage.getContent().stream()
                .map(user -> {
                    RecentCustomerDTO dto = new RecentCustomerDTO();
                    dto.setUserId(user.getId());
                    dto.setName(user.getName());
                    dto.setEmail(user.getEmail());
                    dto.setJoinedDate(user.getCreatedAt().toLocalDate());

                    // Nombre de commandes
                    Long orderCount = orderRepository.countByClientId(user.getId());
                    dto.setOrderCount(orderCount);

                    return dto;
                })
                .collect(Collectors.toList());

        Page<RecentCustomerDTO> dtoPage = new org.springframework.data.domain.PageImpl<>(
                recentCustomerDTOs, pageable, userPage.getTotalElements()
        );

        return new ApiResponse<>(
                "Clients récents",
                new PageResponse<>(dtoPage),
                HttpStatus.OK.value()
        );
    }

    public ApiResponse<Map<String, Object>> getAbandonedCartsRate(LocalDate startDate, LocalDate endDate) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(1);
        long abandonedCarts = cartRepository.findByUpdatedAtBeforeAndOrderedIsFalse(threshold).size();
        long totalCarts = cartRepository.countByOrderedIsFalse();

        double rate = totalCarts > 0 ? (double) abandonedCarts / totalCarts * 100 : 0;

        Map<String, Object> result = new HashMap<>();
        result.put("abandonedCartsRate", rate);
        result.put("abandonedCartsCount", abandonedCarts);
        result.put("totalActiveCarts", totalCarts);

        return new ApiResponse<>("Taux de paniers abandonnés", result, HttpStatus.OK.value());
    }

    public ApiResponse<Map<String, Object>> getConversionRate(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        long totalOrders = (startDateTime != null && endDateTime != null) ?
                orderRepository.countByCreatedAtBetween(startDateTime, endDateTime) : orderRepository.count();

        long totalCarts = (startDateTime != null && endDateTime != null) ?
                cartRepository.countByCreatedAtBetween(startDateTime, endDateTime) : cartRepository.count();

        double rate = totalCarts > 0 ? (double) totalOrders / totalCarts * 100 : 0;

        Map<String, Object> result = new HashMap<>();
        result.put("conversionRate", rate);
        result.put("totalOrders", totalOrders);
        result.put("totalCarts", totalCarts);

        return new ApiResponse<>("Taux de conversion", result, HttpStatus.OK.value());
    }

    public ApiResponse<Map<String, Object>> getPerformanceComparison(
            LocalDate currentStart, LocalDate currentEnd,
            LocalDate previousStart, LocalDate previousEnd) {

        DashboardStatsDTO currentStats = extractStatsFromPeriod(currentStart, currentEnd);
        DashboardStatsDTO previousStats = extractStatsFromPeriod(previousStart, previousEnd);

        Map<String, Object> comparison = new HashMap<>();
        comparison.put("currentPeriod", currentStats);
        comparison.put("previousPeriod", previousStats);

        // Calcul des différences
        Map<String, Double> differences = new HashMap<>();
        differences.put("revenueDiff", calculatePercentageDiff(
                currentStats.getTotalRevenue(),
                previousStats.getTotalRevenue()
        ));
        differences.put("ordersDiff", calculatePercentageDiff(
                BigDecimal.valueOf(currentStats.getTotalOrders()),
                BigDecimal.valueOf(previousStats.getTotalOrders())
        ));
        differences.put("customersDiff", calculatePercentageDiff(
                BigDecimal.valueOf(currentStats.getTotalCustomers()),
                BigDecimal.valueOf(previousStats.getTotalCustomers())
        ));

        comparison.put("differences", differences);

        return new ApiResponse<>("Comparaison des performances", comparison, HttpStatus.OK.value());
    }

    public ApiResponse<Map<String, Object>> getProductStatistics(Long productId) {
        Product product = productRepository.findById(productId).orElse(null);

        if (product == null) {
            return new ApiResponse<>("Produit non trouvé", null, HttpStatus.NOT_FOUND.value());
        }

        Map<String, Object> stats = new HashMap<>();

        // Informations de base
        stats.put("productId", product.getId());
        stats.put("productName", product.getName());
        stats.put("price", BigDecimal.valueOf(product.getPriceCents())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        stats.put("stock", product.getStock());
        stats.put("category", product.getCategory() != null ? product.getCategory().getName() : "Non catégorisé");

        // Statistiques de vente
        long totalQuantitySold = orderRepository.findAll().stream()
                .flatMap(order -> order.getItems().stream())
                .filter(item -> item.getProduct().getId().equals(productId))
                .mapToLong(OrderItem::getQuantity)
                .sum();

        BigDecimal totalRevenue = BigDecimal.valueOf(totalQuantitySold * product.getPriceCents())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        stats.put("totalQuantitySold", totalQuantitySold);
        stats.put("totalRevenue", totalRevenue);

        // Note moyenne (simulée)
        double averageRating = 4.2 + new Random().nextDouble() * 0.8;
        stats.put("averageRating", Math.round(averageRating * 10.0) / 10.0);
        stats.put("reviewsCount", 25 + new Random().nextInt(50));

        return new ApiResponse<>("Statistiques du produit", stats, HttpStatus.OK.value());
    }

    // Méthodes privées utilitaires
    private Page<Order> getOrders(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        if (startDateTime != null && endDateTime != null) {
            return orderRepository.findAllByCreatedAtBetween(startDateTime, endDateTime, pageable);
        } else {
            return orderRepository.findAll(pageable);
        }
    }

    private DashboardStatsDTO extractStatsFromPeriod(LocalDate startDate, LocalDate endDate) {
        List<Order> orders = getOrders(startDate, endDate, Pageable.unpaged()).getContent();

        long totalRevenueCents = orders.stream()
                .mapToLong(Order::getTotalCents)
                .sum();
        BigDecimal totalRevenue = BigDecimal.valueOf(totalRevenueCents)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        long totalOrders = orders.size();

        // Pour cet exemple, on utilise des valeurs simulées
        DashboardStatsDTO stats = new DashboardStatsDTO();
        stats.setTotalRevenue(totalRevenue);
        stats.setTotalOrders(totalOrders);
        stats.setTotalCustomers(100L + new Random().nextInt(200));
        stats.setTotalProducts(productRepository.count());
        stats.setNewCustomers(10L + new Random().nextInt(20));
        stats.setConversionRate(2.5 + new Random().nextDouble() * 3.0);
        stats.setAverageOrderValue(totalOrders > 0 ?
                totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO);

        return stats;
    }

    private double calculatePercentageDiff(BigDecimal current, BigDecimal previous) {
        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}