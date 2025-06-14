package com.group_three.food_ordering.dtos.response;

import com.group_three.food_ordering.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {

    private UUID id;

    private String formattedOrderNumber;

    private String specialRequirements;

    private String clientAlias;

    private BigDecimal totalPrice;

    private OrderStatus status;

    private List<OrderDetailResponseDto> orderDetails;

}
