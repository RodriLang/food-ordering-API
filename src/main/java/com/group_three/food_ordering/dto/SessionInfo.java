package com.group_three.food_ordering.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record SessionInfo(
        UUID userId,
        String subject,
        UUID foodVenueId,
        String role,
        UUID participantId,
        UUID tableSessionId) {
}

