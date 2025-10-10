package com.group_three.food_ordering.analytics.metrics_controllers.impl;

import com.group_three.food_ordering.analytics.metrics_controllers.MetricsController;
import com.group_three.food_ordering.analytics.metrics_dto.*;
import com.group_three.food_ordering.analytics.metrics_services.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MetricsControllerImpl implements MetricsController {

    private final MetricsService metricsService;

    // ---- MÉTRICAS GENERALES ----

    @Override
    public GeneralMetricsResponseDto getGeneralOverview(LocalDateTime from, LocalDateTime to) {
        return metricsService.getGeneralOverview(from, to);
    }

    @Override
    public List<OrdersByVenueDto> getOrdersByVenue(LocalDateTime from, LocalDateTime to) {
        return metricsService.getOrdersByVenue(from, to);
    }

    @Override
    public List<RevenueByVenueDto> getRevenueByVenue(LocalDateTime from, LocalDateTime to) {
        return metricsService.getRevenueByVenue(from, to);
    }

    @Override
    public List<RevenueByVenueDto> getTopVenuesByRevenue(LocalDateTime from, LocalDateTime to, int limit) {
        return metricsService.getTopVenuesByRevenue(from, to, limit);
    }

    // ---- MÉTRICAS CONTEXTUALES (POR LOCAL) ----

    @Override
    public VenueMetricsResponseDto getVenueOverview(UUID venueId, LocalDateTime from, LocalDateTime to) {
        return metricsService.getVenueOverview(venueId, from, to);
    }

    @Override
    public List<TemporalSalesDto> getSalesEvolution(UUID venueId, LocalDateTime from, LocalDateTime to, String groupBy) {
        return metricsService.getSalesEvolution(venueId, from, to, groupBy);
    }

    @Override
    public List<ProductSalesDto> getTopProducts(UUID venueId, LocalDateTime from, LocalDateTime to, int limit) {
        return metricsService.getTopProducts(venueId, from, to, limit);
    }

    @Override
    public List<EmployeePerformanceDto> getEmployeePerformance(UUID venueId, LocalDateTime from, LocalDateTime to) {
        return metricsService.getEmployeePerformance(venueId, from, to);
    }
}
