package com.group_three.food_ordering.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {

    private String orderNumber;

    private String specialRequirements;

    private String clientAlias;

    private List<OrderDetailRequestDto> orderDetails;
}
