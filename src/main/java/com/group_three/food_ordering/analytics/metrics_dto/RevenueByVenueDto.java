package com.group_three.food_ordering.analytics.metrics_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueByVenueDto {

    private UUID venueId;

    private String venueName;

    private BigDecimal totalRevenue;

    private double averageTicket;

}
