package com.group_three.food_ordering.dto;

import com.group_three.food_ordering.dto.response.ParticipantResponseDto;
import com.group_three.food_ordering.models.Participant;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record SessionInfo(

        UUID userId,

        String subject,

        UUID foodVenueId,

        String role,

        UUID participantId,

        // Información de la TableSession en caso de que haya una activa
        UUID tableSessionId,

        Integer tableNumber,

        Integer tableCapacity,

        Instant startTime,

        Instant endTime,

        ParticipantResponseDto hostClient,

        List<Participant> participants) {
}

