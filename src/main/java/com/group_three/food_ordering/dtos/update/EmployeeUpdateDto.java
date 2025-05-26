package com.group_three.food_ordering.dtos.update;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeUpdateDto {

    private UserUpdateDto user;

    @NotBlank(message = "Position is required")
    private String position;
}
