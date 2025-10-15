package com.group_three.food_ordering.dto.request;

import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.utils.OnCreate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequestDto {

    @NotBlank(message = "User email is required", groups = OnCreate.class)
    private String userEmail;

    @NotNull(message = "Position is required", groups = OnCreate.class)
    private RoleType role;

}

