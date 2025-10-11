package com.group_three.food_ordering.analytics.metrics_controllers.impl;

import com.group_three.food_ordering.analytics.enums.TimeBucket;
import com.group_three.food_ordering.analytics.metrics_controllers.MetricsAdminController;
import com.group_three.food_ordering.analytics.metrics_dto.*;
import com.group_three.food_ordering.analytics.metrics_services.MetricsService;
import com.group_three.food_ordering.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
@RestController
@RequiredArgsConstructor
public class MetricsAdminControllerImpl implements MetricsAdminController {

    private final MetricsService metricsService;


    // ---- MÉTRICAS CONTEXTUALES (POR LOCAL) ----

    @Override
    public ResponseEntity<VenueMetricsResponseDto> getVenueOverview(LocalDateTime from, LocalDateTime to) {
        return ResponseEntity.ok(metricsService.getVenueOverview(from, to));
    }

    @Override
    public ResponseEntity<List<TemporalSalesDto>> getSalesEvolution(LocalDateTime from, LocalDateTime to, TimeBucket timeBucket, List<OrderStatus> status) {
        return ResponseEntity.ok(metricsService.getSalesEvolution(from, to, timeBucket, status));
    }

    @Override
    public ResponseEntity<List<ProductSalesDto>> getTopProducts(LocalDateTime from, LocalDateTime to, int limit) {
        return ResponseEntity.ok(metricsService.getTopProducts(from, to, limit));
    }
}
