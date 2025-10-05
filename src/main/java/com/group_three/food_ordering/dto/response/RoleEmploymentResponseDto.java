package com.group_three.food_ordering.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RoleEmploymentResponseDto {

    private UUID publicId;

    private String role;

    private String foodVenueName;
}

