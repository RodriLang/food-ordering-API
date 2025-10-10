package com.group_three.food_ordering.analytics.metrics_services;

import com.group_three.food_ordering.analytics.metrics_dto.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MetricsService {

    // ---- MÉTRICAS GENERALES ----
    GeneralMetricsResponseDto getGeneralOverview(LocalDateTime from, LocalDateTime to);

    List<OrdersByVenueDto> getOrdersByVenue(LocalDateTime from, LocalDateTime to);

    List<RevenueByVenueDto> getRevenueByVenue(LocalDateTime from, LocalDateTime to);

    List<RevenueByVenueDto> getTopVenuesByRevenue(LocalDateTime from, LocalDateTime to, int limit);


    // ---- MÉTRICAS POR LOCAL ----
    VenueMetricsResponseDto getVenueOverview(UUID venueId, LocalDateTime from, LocalDateTime to);

    List<TemporalSalesDto> getSalesEvolution(UUID venueId, LocalDateTime from, LocalDateTime to, String groupBy);

    List<ProductSalesDto> getTopProducts(UUID venueId, LocalDateTime from, LocalDateTime to, int limit);

    List<EmployeePerformanceDto> getEmployeePerformance(UUID venueId, LocalDateTime from, LocalDateTime to);
}
