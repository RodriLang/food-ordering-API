package com.group_three.food_ordering.analytics.metrics_controllers.impl;

import com.group_three.food_ordering.analytics.metrics_controllers.MetricsRootController;
import com.group_three.food_ordering.analytics.metrics_dto.*;
import com.group_three.food_ordering.analytics.metrics_services.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@PreAuthorize("hasRole('ROOT')")
@RestController
@RequiredArgsConstructor
public class MetricsRootControllerImpl implements MetricsRootController {

    private final MetricsService metricsService;

    // ---- MÉTRICAS GENERALES ----

    @Override
    public ResponseEntity<GeneralMetricsResponseDto> getGeneralOverview(Instant from, Instant to) {
        return ResponseEntity.ok(metricsService.getGeneralOverview(from, to));
    }

    @Override
    public ResponseEntity<List<OrdersByVenueDto>> getOrdersByVenue(Instant from, Instant to) {
        return ResponseEntity.ok(metricsService.getOrdersByVenue(from, to));
    }

    @Override
    public ResponseEntity<List<RevenueByVenueDto>> getRevenueByVenue(Instant from, Instant to) {
        return ResponseEntity.ok(metricsService.getRevenueByVenue(from, to));
    }

    @Override
    public ResponseEntity<List<RevenueByVenueDto>> getTopVenuesByRevenue(Instant from, Instant to, int limit) {
        return ResponseEntity.ok(metricsService.getTopVenuesByRevenue(from, to, limit));
    }

}
