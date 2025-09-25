package com.group_three.food_ordering.dto.create;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeCreateDto {

    private UUID userId;

    @Valid
    private UserCreateDto user;

    @NotBlank(message = "Position is required")
    private String position;

    @NotNull(message = "Food venue ID is required")
    private UUID foodVenueId;
}
