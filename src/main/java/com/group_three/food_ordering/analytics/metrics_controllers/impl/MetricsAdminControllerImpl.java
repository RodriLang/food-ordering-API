package com.group_three.food_ordering.analytics.metrics_controllers.impl;

import com.group_three.food_ordering.analytics.metrics_controllers.MetricsAdminController;
import com.group_three.food_ordering.analytics.metrics_dto.*;
import com.group_three.food_ordering.analytics.metrics_services.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MetricsAdminControllerImpl implements MetricsAdminController {

    private final MetricsService metricsService;


    // ---- MÉTRICAS CONTEXTUALES (POR LOCAL) ----

    @Override
    public ResponseEntity<VenueMetricsResponseDto> getVenueOverview(UUID venueId, LocalDateTime from, LocalDateTime to) {
        return ResponseEntity.ok(metricsService.getVenueOverview(venueId, from, to));
    }

    @Override
    public ResponseEntity<List<TemporalSalesDto>> getSalesEvolution(UUID venueId, LocalDateTime from, LocalDateTime to, String groupBy) {
        return ResponseEntity.ok(metricsService.getSalesEvolution(venueId, from, to, groupBy));
    }

    @Override
    public ResponseEntity<List<ProductSalesDto>> getTopProducts(UUID venueId, LocalDateTime from, LocalDateTime to, int limit) {
        return ResponseEntity.ok(metricsService.getTopProducts(venueId, from, to, limit));
    }

    @Override
    public ResponseEntity<List<EmployeePerformanceDto>> getEmployeePerformance(UUID venueId, LocalDateTime from, LocalDateTime to) {
        return ResponseEntity.ok(metricsService.getEmployeePerformance(venueId, from, to));
    }
}
