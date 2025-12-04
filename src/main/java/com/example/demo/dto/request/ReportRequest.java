package com.example.demo.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Classes DTO pour les requêtes
 */
@Setter
@Getter
class ReportRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> columns;
    private Map<String, Object> filters;
    private String reportType;
    private boolean includeCharts;
    private String groupBy;

}