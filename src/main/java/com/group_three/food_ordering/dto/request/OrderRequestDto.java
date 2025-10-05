package com.group_three.food_ordering.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {

    @Valid
    @NotEmpty(message = "Order must have at least one detail")
    private List<OrderDetailRequestDto> orderDetails = new ArrayList<>();

    @Size(max = 255, message = "Special requirements must be 255 characters or less")
    private String specialRequirements;

}

