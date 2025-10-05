package com.group_three.food_ordering.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class EmploymentResponseDto {

    private UUID publicId;

    private UserResponseDto user;

    private String role;

    private String foodVenue;
}

