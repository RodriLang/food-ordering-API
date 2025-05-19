package com.group_three.food_ordering.dtos.update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TableSessionUpdateDto {
    private UUID sessionId;
    private LocalDateTime endTime;
    private List<UUID> participantIds;
}
