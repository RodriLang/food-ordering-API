package com.group_three.food_ordering.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableSessionResponseDto {

    private UUID publicId;

    private Instant startTime;

    private Instant endTime;

    private Integer tableNumber;

    private String tableStatus;

    private Integer tableCapacity;

    private Integer numberOfParticipants;

    private Boolean isHostClient;

}
