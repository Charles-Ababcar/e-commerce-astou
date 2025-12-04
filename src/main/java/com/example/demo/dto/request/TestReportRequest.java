package com.example.demo.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

@Setter
@Getter
class TestReportRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<String, Object> filters;

}