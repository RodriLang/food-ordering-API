package com.group_three.food_ordering.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {

    private String formattedOrderNumber;

    private String specialRequirements;

    private String clientAlias;

    private BigDecimal totalPrice;

    private List<OrderDetailResponseDto> orderDetails;

}
