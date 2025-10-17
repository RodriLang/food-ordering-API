package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.SessionInfo;
import com.group_three.food_ordering.dto.request.LoginRequest;
import com.group_three.food_ordering.dto.request.RefreshTokenRequest;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.dto.response.ParticipantResponseDto;
import com.group_three.food_ordering.dto.response.RoleEmploymentResponseDto;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.exceptions.InvalidTokenException;
import com.group_three.food_ordering.mappers.ParticipantMapper;
import com.group_three.food_ordering.mappers.RoleEmploymentMapper;
import com.group_three.food_ordering.models.*;
import com.group_three.food_ordering.repositories.*;
import com.group_three.food_ordering.security.JwtService;
import com.group_three.food_ordering.security.RefreshTokenService;
import com.group_three.food_ordering.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.group_three.food_ordering.utils.EntityName.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ParticipantMapper participantMapper;
    private final TableSessionRepository tableSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleEmploymentMapper roleEmploymentMapper;
    private final RefreshTokenService refreshTokenService;
    private final TenantContext tenantContext;

    // =====================================
    // Public API
    // =====================================

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        log.debug("[AuthService] Login request received");
        User loggedUser = authenticateUser(loginRequest);
        log.debug("[AuthService] Logged in user={}", loggedUser.getEmail());

        // Construir SessionInfo acorde a la situación actual del usuario
        SessionInfo sessionInfo = resolveSessionInfo(loggedUser);

        log.debug("[JwtService] Generating access token for user {}", loggedUser.getEmail());
        String accessToken = jwtService.generateAccessToken(sessionInfo);

        log.debug("[RefreshTokenService] Generating refresh token for user {}", loggedUser.getEmail());
        String refreshToken = refreshTokenService.generateRefreshToken(loggedUser.getEmail());

        return createLoginResponse(loggedUser, accessToken, refreshToken, sessionInfo);
    }

    @Override
    public void logout(String refreshToken) {
        if (refreshToken != null) {
            log.debug("[RefreshTokenService] Calling revokeToken for refresh token");
            refreshTokenService.revokeToken(refreshToken);
        }
    }

    @Override
    public AuthResponse refreshAccessToken(RefreshTokenRequest request) {
        log.debug("[RefreshTokenService] Calling validateAndGetUserEmail");
        String userEmail = refreshTokenService.validateAndGetUserEmail(request.refreshToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired refresh token"));
        log.debug("[AuthService] Refresh token request for user={}", userEmail);

        log.debug("[UserRepository] Calling findByEmail for user {}", userEmail);
        User user = userRepository.findByEmailAndDeletedFalse(userEmail)
                .orElseThrow(() -> new EntityNotFoundException(USER));

        SessionInfo sessionInfo = resolveSessionInfo(user);

        log.debug("[JwtService] Generating new access token for user {}", userEmail);
        String newAccessToken = jwtService.generateAccessToken(sessionInfo);

        log.debug("[RefreshTokenService] Generating new refresh token for user {}", userEmail);
        String newRefreshToken = refreshTokenService.generateRefreshToken(userEmail);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .role(sessionInfo.role())
                .build();
    }

    // =====================================
    // Internals
    // =====================================

    private User authenticateUser(LoginRequest loginRequest) {
        log.debug("[AuthService] Authenticating user email={}", loginRequest.getEmail());

        log.debug("[UserRepository] Calling findByEmailWithEmployments for authentication user {}", loginRequest.getEmail());
        User user = userRepository.findByEmailWithEmployments(loginRequest.getEmail())
                .orElseThrow(() -> {
                    log.warn("[AuthService] User not found for email={}", loginRequest.getEmail());
                    return new UsernameNotFoundException("Usuario o contraseña incorrectos");
                });

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.warn("[AuthService] User authentication failed for email={}", loginRequest.getEmail());
            throw new BadCredentialsException("Usuario o contraseña incorrectos");
        }
        log.debug("[AuthService] Authenticated user: {}", user.getEmail());
        return user;
    }

    /**
     * Construye el snapshot de SessionInfo para token:
     * - Si el usuario tiene sesión activa → usa esa.
     * - Si no, arma una sesión “limpia” basada en el contexto (tenant opcional, mesa/participant si ya venían).
     */
    private SessionInfo resolveSessionInfo(User loggedUser) {
        log.debug("[AuthService] Resolving session info for user={}", loggedUser.getEmail());

        // 1) ¿Usuario con sesión activa en DB?
        log.debug("[TableSessionRepository] Calling findActiveSessionByUserEmailAndDeletedFalse for user {}",
                loggedUser.getEmail());

        Optional<TableSession> activeOpt = tableSessionRepository
                .findActiveSessionByUserEmailAndDeletedFalse(loggedUser.getEmail());

        if (activeOpt.isPresent()) {
            TableSession active = activeOpt.get();
            log.debug("[AuthService] Found active session={} for user={}", active.getPublicId(), loggedUser.getEmail());
            return createSessionInfoFromActiveSession(active, loggedUser);
        }

        // 2) Usuario sin sesión activa → sesión limpia. (Si el request venía con datos de guest o tenant, los re-usa)
        log.debug("[AuthService] No active session for {}. Building clean session info.", loggedUser.getEmail());
        return createSessionInfoForLoggedUser(loggedUser);
    }

    private SessionInfo createSessionInfoFromActiveSession(TableSession tableSession, User loggedUser) {
        ParticipantResponseDto host = participantMapper.toResponseDto(tableSession.getSessionHost());
        List<ParticipantResponseDto> participants = tableSession.getParticipants().stream()
                .map(participantMapper::toResponseDto)
                .toList();

        return SessionInfo.builder()
                .userId(loggedUser.getPublicId())
                .subject(loggedUser.getEmail())
                .foodVenueId(tableSession.getFoodVenue().getPublicId())
                .participantId(findParticipantIdForUser(tableSession, loggedUser))
                .tableSessionId(tableSession.getPublicId())
                .role(RoleType.ROLE_CLIENT.name())
                .startTime(tableSession.getStartTime())
                .endTime(tableSession.getEndTime())
                .hostClient(host)
                .participants(participants)
                .tableNumber(tableSession.getDiningTable().getNumber())
                .build();
    }

    /**
     * Crea un SessionInfo “limpio” para un usuario logueado sin mesa activa.
     * Usa datos del contexto si existen (tenant, participant/mesa de un token previo), pero
     * NO hace consultoría de negocio (no crea ni migra nada).
     */
    private SessionInfo createSessionInfoForLoggedUser(User loggedUser) {
        UUID venueId = tenantContext.getFoodVenueId();

        UUID participantId = tenantContext.getParticipantId();

        UUID tableSessionId = tenantContext.getTableSessionId();

        return SessionInfo.builder()
                .userId(loggedUser.getPublicId())
                .subject(loggedUser.getEmail())
                .foodVenueId(venueId)           // puede ser null
                .participantId(participantId)   // puede ser null (sin mesa)
                .tableSessionId(tableSessionId) // puede ser null (sin mesa)
                .role(RoleType.ROLE_CLIENT.name())
                .build();
    }

    private UUID findParticipantIdForUser(TableSession ts, User user) {
        return ts.getParticipants().stream()
                .filter(p -> p.getUser() != null && user.getEmail().equals(p.getUser().getEmail()))
                .map(Participant::getPublicId)
                .findFirst()
                .orElse(null);
    }

    private AuthResponse createLoginResponse(
            User loggedUser, String accessToken, String refreshToken, SessionInfo sessionInfo) {

        log.debug("[AuthService] Generating login response");
        Instant expiration = jwtService.getExpirationDateFromToken(accessToken);

        List<RoleEmploymentResponseDto> employments =
                (loggedUser.getEmployments() == null || loggedUser.getEmployments().isEmpty())
                        ? null
                        : loggedUser.getEmployments().stream()
                        .filter(e -> Boolean.TRUE.equals(e.getActive()))
                        .map(roleEmploymentMapper::toResponseDto)
                        .toList();

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expirationDate(expiration)
                .employments(employments)
                .startTime(sessionInfo.startTime())
                .endTime(sessionInfo.endTime())
                .hostClient(sessionInfo.hostClient())
                .participants(sessionInfo.participants())
                .tableNumber(sessionInfo.tableNumber())
                .numberOfParticipants(sessionInfo.participants() != null ? sessionInfo.participants().size() : null)
                .role(sessionInfo.role())
                .build();

        log.debug("[AuthService] Auth response generated");
        return authResponse;
    }
}
