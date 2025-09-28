package com.group_three.food_ordering.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantResponseDto {
    private UUID id;
    private UserResponseDto user;
    private String nickname;
}
