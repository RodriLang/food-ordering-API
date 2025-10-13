package com.group_three.food_ordering.analytics.metrics_services;

import com.group_three.food_ordering.analytics.enums.TimeBucket;
import com.group_three.food_ordering.analytics.metrics_dto.*;
import com.group_three.food_ordering.enums.OrderStatus;

import java.time.Instant;
import java.util.List;

public interface MetricsService {

    // ---- MÉTRICAS GENERALES ----
    GeneralMetricsResponseDto getGeneralOverview(Instant from, Instant to);

    List<OrdersByVenueDto> getOrdersByVenue(Instant from, Instant to);

    List<RevenueByVenueDto> getRevenueByVenue(Instant from, Instant to);

    List<RevenueByVenueDto> getTopVenuesByRevenue(Instant from, Instant to, int limit);


    // ---- MÉTRICAS POR LOCAL ----

    VenueMetricsResponseDto getVenueOverview(Instant from, Instant to);

    List<TemporalSalesDto> getSalesEvolution(
            Instant from,
            Instant to,
            TimeBucket timeBucket,
            List<OrderStatus> statuses);

    List<ProductSalesDto> getTopProducts(Instant from, Instant to, int limit);
}
