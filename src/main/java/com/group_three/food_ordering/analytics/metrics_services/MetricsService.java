package com.group_three.food_ordering.analytics.metrics_services;

import com.group_three.food_ordering.analytics.enums.TimeBucket;
import com.group_three.food_ordering.analytics.metrics_dto.*;
import com.group_three.food_ordering.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface MetricsService {

    // ---- MÉTRICAS GENERALES ----
    GeneralMetricsResponseDto getGeneralOverview(LocalDateTime from, LocalDateTime to);

    List<OrdersByVenueDto> getOrdersByVenue(LocalDateTime from, LocalDateTime to);

    List<RevenueByVenueDto> getRevenueByVenue(LocalDateTime from, LocalDateTime to);

    List<RevenueByVenueDto> getTopVenuesByRevenue(LocalDateTime from, LocalDateTime to, int limit);


    // ---- MÉTRICAS POR LOCAL ----

    VenueMetricsResponseDto getVenueOverview(LocalDateTime from, LocalDateTime to);

    List<TemporalSalesDto> getSalesEvolution(
            LocalDateTime from,
            LocalDateTime to,
            TimeBucket timeBucket,
            List<OrderStatus> statuses);

    List<ProductSalesDto> getTopProducts(LocalDateTime from, LocalDateTime to, int limit);
}
