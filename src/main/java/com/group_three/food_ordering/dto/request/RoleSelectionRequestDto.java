package com.group_three.food_ordering.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RoleSelectionRequestDto(

        @NotNull(message = "Employment ID is required")
        UUID employmentId

) {
}
