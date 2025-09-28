package com.group_three.food_ordering.dto;

import java.util.UUID;

public record SessionInfo(
        UUID foodVenueId,
        UUID participantId,
        UUID tableSessionId) {
}

