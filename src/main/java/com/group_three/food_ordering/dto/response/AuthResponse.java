package com.group_three.food_ordering.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AuthResponse {

    //Datos de la TableSession
    private Integer tableNumber;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private ParticipantResponseDto hostClient;

    private List<ParticipantResponseDto> participants;

    //Datos de autenticación
    private String accessToken;

    private String refreshToken;

    private Instant expirationDate;

    //Roles disponibles por Employments
    private List<RoleEmploymentResponseDto> employments;

}