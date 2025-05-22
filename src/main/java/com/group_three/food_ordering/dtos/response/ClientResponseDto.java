package com.group_three.food_ordering.dtos.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponseDto {
    private UserResponseDto user;
    private String nickname;
}
