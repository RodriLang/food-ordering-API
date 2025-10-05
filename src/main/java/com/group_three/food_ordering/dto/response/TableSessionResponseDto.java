package com.group_three.food_ordering.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableSessionResponseDto {

    private UUID publicId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer tableNumber;

    private ParticipantResponseDto hostClient;

    private List<ParticipantResponseDto> participants;

}
