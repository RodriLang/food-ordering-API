package com.group_three.food_ordering.dtos.create;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeCreateDto {

    @Valid
    @NotNull(message = "User information is required")
    private UserCreateDto user;

    @NotBlank(message = "Position is required")
    private String position;

    @NotNull(message = "Food venue ID is required")
    private UUID foodVenueId;
}
