package com.group_three.food_ordering.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateDto {

    @Size(max = 255, message = "Special requirements must be 255 characters or less")
    private String specialRequirements;

    @NotNull(message = "Food venue is required")
    private FoodVenueRequestDto foodVenue;

    @NotNull(message = "Client is required")
    private ClientRequestDto client;
}
