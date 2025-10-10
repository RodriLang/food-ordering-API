package com.group_three.food_ordering.analytics.metrics_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemporalSalesDto {

    private String label;// día, semana o mes

    private long totalOrders;

    private BigDecimal totalRevenue;

}
