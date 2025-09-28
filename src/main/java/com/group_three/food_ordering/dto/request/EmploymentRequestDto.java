package com.group_three.food_ordering.dto.request;

import com.group_three.food_ordering.enums.RoleType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmploymentRequestDto {

    @Valid
    private UUID userId;

    @NotBlank(message = "Position is required")
    private RoleType role;

    @NotNull(message = "Food venue ID is required")
    private UUID foodVenueId;
}

