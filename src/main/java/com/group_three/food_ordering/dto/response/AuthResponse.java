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

    private Integer tableCapacity;

    private Instant startTime;

    private Instant endTime;

    private Integer numberOfParticipants;

    private Boolean isHostClient;

    private List<ParticipantResponseDto> activeParticipants;

    private List<ParticipantResponseDto> previousParticipants;

    //Datos de autenticación
    private String accessToken;

    private String refreshToken;

    private Instant expirationDate;

    private String role;

    //Roles disponibles por Employments
    private List<RoleEmploymentResponseDto> employments;

}