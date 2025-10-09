package com.group_three.food_ordering.dto;

import com.group_three.food_ordering.dto.response.ParticipantResponseDto;
import lombok.Builder;

import java.time.LocalDateTime;
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

        LocalDateTime startTime,

        LocalDateTime endTime,

        ParticipantResponseDto hostClient,

        List<ParticipantResponseDto> participants) {
}

