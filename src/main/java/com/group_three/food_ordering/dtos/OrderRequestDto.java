package com.group_three.food_ordering.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {

    @Size(max = 255, message = "Special requirements must be 255 characters or less")
    private String specialRequirements;

    @NotNull(message = "Food venue is required")
    private UUID foodVenueId;

    @NotNull(message = "Client is required")
    private UUID clientId;

    @Valid
    @NotEmpty(message = "Order must have at least one detail")
    private List<OrderDetailRequestDto> orderDetails = new ArrayList<>();
}

