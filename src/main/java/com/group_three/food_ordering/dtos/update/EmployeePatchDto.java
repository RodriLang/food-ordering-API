package com.group_three.food_ordering.dtos.update;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeePatchDto {
    private String position;
    private UserPatchDto user;
}
