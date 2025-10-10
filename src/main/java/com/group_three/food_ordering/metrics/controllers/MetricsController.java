package com.group_three.food_ordering.metrics.controllers;

import com.group_three.food_ordering.metrics.dto.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/metrics")
public interface MetricsController {

    // ---- MÉTRICAS GENERALES (ROOT / ADMIN) ----

    @GetMapping("/general/overview")
    GeneralMetricsResponseDto getGeneralOverview(
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to
    );

    @GetMapping("/general/orders")
    List<OrdersByVenueDto> getOrdersByVenue(
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to
    );

    @GetMapping("/general/revenue")
    List<RevenueByVenueDto> getRevenueByVenue(
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to
    );

    @GetMapping("/general/top-venues")
    List<RevenueByVenueDto> getTopVenuesByRevenue(
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to,
            @RequestParam(defaultValue = "5") int limit
    );


    // ---- MÉTRICAS POR LOCAL (CONTEXTUALES) ----

    @GetMapping("/venue/{venueId}/overview")
    VenueMetricsResponseDto getVenueOverview(
            @PathVariable UUID venueId,
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to
    );

    @GetMapping("/venue/{venueId}/sales")
    List<TemporalSalesDto> getSalesEvolution(
            @PathVariable UUID venueId,
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to,
            @RequestParam(defaultValue = "day") String groupBy
    );

    @GetMapping("/venue/{venueId}/top-products")
    List<ProductSalesDto> getTopProducts(
            @PathVariable UUID venueId,
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to,
            @RequestParam(defaultValue = "5") int limit
    );

    @GetMapping("/venue/{venueId}/employees")
    List<EmployeePerformanceDto> getEmployeePerformance(
            @PathVariable UUID venueId,
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to
    );
}
