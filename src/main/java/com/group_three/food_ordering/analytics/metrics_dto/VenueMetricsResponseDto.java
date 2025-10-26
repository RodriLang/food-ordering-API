package com.group_three.food_ordering.analytics.metrics_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VenueMetricsResponseDto {

    private UUID venueId;

    private String venueName;

    private long totalOrders;

    private BigDecimal totalRevenue;

    private double averageTicket;

    private double averageSessionDurationMinutes;

    private double averageSpendingPerTable;

    private double cancellationRate;

}
