package com.group_three.food_ordering.dto.request;

import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.utils.validations.AllowedRoles;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequestDto {

    @Valid
    private String userEmail;

    @NotBlank(message = "Position is required")
    @AllowedRoles({RoleType.ROLE_STAFF, RoleType.ROLE_MANAGER})
    private RoleType role;

}

