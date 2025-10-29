package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.SessionInfo;
import com.group_three.food_ordering.dto.request.LoginRequest;
import com.group_three.food_ordering.dto.request.RefreshTokenRequest;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.dto.response.ParticipantResponseDto;
import com.group_three.food_ordering.dto.response.RoleEmploymentResponseDto;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.exceptions.InvalidTokenException;
import com.group_three.food_ordering.exceptions.UserSessionConflictException;
import com.group_three.food_ordering.mappers.ParticipantMapper;
import com.group_three.food_ordering.mappers.RoleEmploymentMapper;
import com.group_three.food_ordering.models.*;
import com.group_three.food_ordering.notifications.SseEventType;
import com.group_three.food_ordering.notifications.SseService;
import com.group_three.food_ordering.repositories.*;
import com.group_three.food_ordering.security.JwtService;
import com.group_three.food_ordering.security.RefreshTokenService;
import com.group_three.food_ordering.services.AuthService;
import com.group_three.food_ordering.services.OrderService;
import com.group_three.food_ordering.services.ParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
    private final ParticipantService participantService;
    private final OrderService orderService;
    private final SseService sseService;
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

        SessionInfo sessionInfo = tenantContext.session();

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

        Optional<Participant> guestOpt = tenantContext.participantOpt();
        Optional<TableSession> guestSessionOpt = tenantContext.tableSessionOpt();

        Optional<TableSession> activeOpt = tableSessionRepository
                .findActiveSessionByUserEmailAndDeletedFalse(loggedUser.getEmail());

        // === Caso 1: Usuario ya tiene sesión activa y no viene desde una mesa ===
        if (guestOpt.isEmpty() && activeOpt.isPresent()) {
            log.debug("[AuthService] Found active session for user={}, returning existing session", loggedUser.getEmail());
            return createSessionInfoFromActiveSession(activeOpt.get(), loggedUser);
        }

        // === Caso 2: Usuario sin sesión activa, pero está en una mesa como invitado ===
        if (guestOpt.isPresent() && activeOpt.isEmpty()) {
            Participant guest = guestOpt.get();
            log.debug("[AuthService] Migrating guest participant {} -> user {}", guest.getPublicId(), loggedUser.getEmail());
            migrateGuestToClient(guest, loggedUser);
            return createSessionInfoFromActiveSession(guest.getTableSession(), loggedUser);
        }

        // === Caso 3: Usuario con sesión activa + sesión de invitado simultánea ===
        if (guestOpt.isPresent()) {
            TableSession active = activeOpt.get();
            TableSession guestSession = guestSessionOpt.orElse(null);

            if (guestSession == null) {
                log.warn("[AuthService] Guest participant has no table session. Defaulting to active one.");
                return createSessionInfoFromActiveSession(active, loggedUser);
            }

            if (active.getPublicId().equals(guestSession.getPublicId())) {
                log.debug("[AuthService] Same table session detected. Merging guest orders into existing participant.");
                mergeGuestParticipant(guestOpt.get(), active, loggedUser);
                return createSessionInfoFromActiveSession(active, loggedUser);
            } else {
                log.warn("[AuthService] Conflict: user has active session {} but trying to log in from another session {}",
                        active.getPublicId(), guestSession.getPublicId());
                throw new UserSessionConflictException("User already has an active session on another table.");
            }
        }

        // === Caso 4: Sin sesión activa ni invitado (login limpio) ===
        log.debug("[AuthService] No active or guest session found for user={}", loggedUser.getEmail());
        return createSessionInfoForLoggedUser(loggedUser);
    }

    private void migrateGuestToClient(Participant guest, User loggedUser) {
        participantService.update(guest, loggedUser);
        log.debug("[AuthService] Guest participant migrated to CLIENT: {}", guest.getPublicId());
    }

    private void mergeGuestParticipant(Participant guest, TableSession session, User loggedUser) {
        Participant existing = session.getParticipants().stream()
                .filter(p -> p.getUser() != null && p.getUser().equals(loggedUser))
                .findFirst()
                .orElse(null);

        if (existing == null) {
            // No había participante “real”: convertir invitado en cliente
            participantService.update(guest, loggedUser);
            log.debug("[AuthService] No existing participant found. Guest {} converted to CLIENT.", guest.getPublicId());
            return;
        }

        // Reasignar pedidos
        Integer moved = orderService.reassignOrdersToParticipant(guest, existing);

        // Eliminar participante invitado
        boolean removed = session.getParticipants().remove(guest);
        participantService.softDelete(guest.getPublicId());

        log.debug("[AuthService] Merged guest {} into existing {}. Orders moved={}",
                guest.getPublicId(), existing.getPublicId(), moved);

        if(removed){
            int newParticipantCount = session.getParticipants().size();
            String tableSessionId = session.getPublicId().toString();
            log.debug("[AuthService] Sending COUNT_UPDATED event after merge. Session: {}, New Count: {}",
                    tableSessionId, newParticipantCount);

            sseService.sendEventToTableSession(
                    tableSessionId,
                    SseEventType.COUNT_UPDATED,
                    Map.of("count", newParticipantCount)
            );
        }
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
                .tableCapacity(tableSession.getDiningTable().getCapacity())
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
        UUID currentParticipantId = tenantContext.getParticipantId();
        Boolean isHostClient = sessionInfo.participantId().equals(currentParticipantId);

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
                .isHostClient(isHostClient)
                .tableNumber(sessionInfo.tableNumber())
                .tableCapacity(sessionInfo.tableCapacity())
                .numberOfParticipants(sessionInfo.participants() != null ? sessionInfo.participants().size() : null)
                .role(sessionInfo.role())
                .build();

        log.debug("[AuthService] Auth response generated");
        return authResponse;
    }
}
