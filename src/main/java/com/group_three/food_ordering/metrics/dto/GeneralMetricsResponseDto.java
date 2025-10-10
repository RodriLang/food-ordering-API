package com.group_three.food_ordering.metrics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralMetricsResponseDto {
    private long totalOrders;
    private long totalVenues;
    private BigDecimal totalRevenue;
    private double averageTicket;
    private double averageSessionDurationMinutes;
}
