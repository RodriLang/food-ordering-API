package com.group_three.food_ordering.dto.request;

import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.utils.OnCreate;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmploymentRequestDto {

    @NotBlank(message = "User email is required", groups = OnCreate.class)
    private String userEmail;

    @NotNull(message = "Food venue ID is required", groups = OnCreate.class)
    private UUID foodVenueId;

    @NotBlank(message = "Position is required", groups = OnCreate.class)
    private RoleType role;

}

