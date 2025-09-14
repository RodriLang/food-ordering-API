package com.group_three.food_ordering.dto.update;

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
