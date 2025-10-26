package com.group_three.food_ordering.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AuthResponse {

    //Datos de la TableSession
    private Integer tableNumber;

    private Instant startTime;

    private Instant endTime;

    private Integer numberOfParticipants;

    private ParticipantResponseDto hostClient;

    private List<ParticipantResponseDto> participants;

    //Datos de autenticación
    private String accessToken;

    private String refreshToken;

    private Instant expirationDate;

    private String role;

    //Roles disponibles por Employments
    private List<RoleEmploymentResponseDto> employments;

}