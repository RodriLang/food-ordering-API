package com.group_three.food_ordering.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class UserCardResponseDto {

    private UUID publicId;

    private String name;

    private String lastName;

    private String email;

}