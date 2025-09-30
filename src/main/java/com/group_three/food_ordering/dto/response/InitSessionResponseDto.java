package com.group_three.food_ordering.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitSessionResponseDto {

    private Integer tableNumber;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private ParticipantResponseDto hostClient;

    private List<ParticipantResponseDto> participants;

    private String accessToken;

}
