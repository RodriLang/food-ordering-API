package com.group_three.food_ordering.dtos.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponseDto {
    private UUID id;
    private UserResponseDto user;
    private String nickname;
}
