package com.group_three.food_ordering.dtos.create;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableCreateDto {

    @NotNull(message = "Food Venue ID is required")
    private UUID foodVenueId;

    @NotNull(message = "Number is required")
    private Integer number;

    @NotNull(message = "Capacity is required")
    private Integer capacity;
}
