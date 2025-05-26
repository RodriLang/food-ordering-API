package com.group_three.food_ordering.dtos.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class EmployeeResponseDto {
    private UUID id;
    private UserResponseDto user;
    private String position;
    private String foodVenueName;
}
