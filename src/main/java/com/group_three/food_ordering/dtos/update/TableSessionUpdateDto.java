package com.group_three.food_ordering.dtos.update;

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
public class TableSessionUpdateDto {
    private UUID id;
    private LocalDateTime endTime;
    private List<UUID> participantIds;
}
