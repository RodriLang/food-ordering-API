package com.group_three.food_ordering.dtos.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class EmployeeResponseDto {
    private UserResponseDto user;
    private String position;
    private String foodVenueName;
}
