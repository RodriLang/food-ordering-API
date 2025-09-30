package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.SessionInfo;
import com.group_three.food_ordering.dto.request.LoginRequest;
import com.group_three.food_ordering.dto.request.RefreshTokenRequest;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.dto.response.RoleSelectionResponseDto;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.exceptions.InvalidTokenException;
import com.group_three.food_ordering.models.*;
import com.group_three.food_ordering.repositories.*;
import com.group_three.food_ordering.security.CustomUserPrincipal;
import com.group_three.food_ordering.security.JwtService;
import com.group_three.food_ordering.dto.response.LoginResponse;
import com.group_three.food_ordering.services.AuthService;
import com.group_three.food_ordering.services.ParticipantService;
import com.group_three.food_ordering.services.RefreshTokenService;
import com.group_three.food_ordering.services.RoleSelectionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final ParticipantService participantService;
    private final TableSessionRepository tableSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final HttpServletRequest request;
    private final RoleSelectionService roleSelectionService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User loggedUser = authenticateUser(loginRequest);
        SessionInfo sessionInfo = resolveSessionInfo(loggedUser);
        String accessToken = createAccessToken(loggedUser, sessionInfo);
        String refreshToken = createRefreshToken(loggedUser);
        return createLoginResponse(loggedUser, accessToken, refreshToken);
    }

    @Override
    public void logout(String refreshToken) {
        if (refreshToken != null) {
            refreshTokenService.revokeToken(refreshToken);
        }
    }

    @Override
    public AuthResponse refreshAccessToken(RefreshTokenRequest request) {
        String userEmail = refreshTokenService.validateAndGetUserEmail(request.refreshToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired refresh token"));
        log.debug("[AuthService] Refresh token request for user={}", userEmail);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        SessionInfo sessionInfo = resolveSessionInfo(user);

        String newAccessToken = createAccessToken(user, sessionInfo);

        String newRefreshToken = refreshTokenService.generateRefreshToken(userEmail);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .accessTokenExpiresAt(jwtService.getExpirationDateFromToken(newAccessToken))
                .build();
    }

    @Override
    public Optional<User> getAuthUser() {
        log.debug("[AuthService] Getting authenticated user from principal");
        Optional<CustomUserPrincipal> principalOpt = getCurrentPrincipal();
        if (principalOpt.isEmpty()) {
            log.debug("[AuthService] No authenticated principal found");
            return Optional.empty();
        }
        return getCurrentPrincipal()
                .map(CustomUserPrincipal::getEmail)
                .flatMap(userRepository::findByEmail);
    }

    @Override
    public Optional<Participant> getCurrentParticipant() {
        log.debug("[AuthService] Getting current participant from principal");
        return getCurrentPrincipal()
                .map(CustomUserPrincipal::getParticipantId)
                .flatMap(participantRepository::findById);
    }

    @Override
    public Optional<TableSession> getCurrentTableSession() {
        log.debug("[AuthService] Getting current table session from principal");
        return getCurrentPrincipal()
                .map(CustomUserPrincipal::getTableSessionId)
                .flatMap(tableSessionRepository::findById);
    }


    private User authenticateUser(LoginRequest loginRequest) {
        User loggedUser = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), loggedUser.getPassword())) {
            log.warn("[AuthController] User authentication failed for email={}", loginRequest.getEmail());
            throw new BadCredentialsException("Usuario o contraseña incorrectos");
        }
        log.debug("[AuthService] Authenticated user: {}", loggedUser.getEmail());
        return loggedUser;
    }

    private SessionInfo resolveSessionInfo(User loggedUser) {
        log.debug("[AuthService] Resolving session info for user={}", loggedUser.getEmail());

        // 1. Usuario registrado con sesión activa en DB
        Optional<TableSession> previousActiveSession =
                tableSessionRepository.findActiveSessionByUserEmail(loggedUser.getEmail());
        if (previousActiveSession.isPresent()) {
            log.debug("[AuthService] Found active session={} for user={}",
                    previousActiveSession.get().getId(), loggedUser.getEmail());
            return createSessionInfoFromActiveSession(previousActiveSession.get(), loggedUser);
        }

        // 2. Usuario que viene de invitado → promover invitado a cliente
        Optional<SessionInfo> guestSessionInfo = extractGuestSessionFromRequest();
        log.debug("[AuthService] Verifying guest session in request");

        if (guestSessionInfo.isPresent()) {
            SessionInfo guestInfo = guestSessionInfo.get();
            log.debug("[AuthService] Promoting guest session to client for user={}, tableSessionId={}",
                    loggedUser.getEmail(), guestInfo.tableSessionId());

            return promoteGuestSessionToClient(loggedUser, guestInfo);
        }


        // 3. Usuario limpio → generar session info sin mesa
        log.debug("[AuthService] No active or guest session for user={}. Creating clean session.",
                loggedUser.getEmail());
        return createSessionInfoForLoggedUser(loggedUser);
    }

    private Optional<SessionInfo> extractGuestSessionFromRequest() {
        String token = extractTokenFromRequest();
        if (token == null) {
            log.debug("[AuthService] No Authorization header with Bearer token found in request");
            return Optional.empty();
        }
        try {
            SessionInfo sessionInfo = jwtService.getSessionInfoFromToken(token);

            // Solo extraer si es un token de invitado
            if (!"ROLE_GUEST".equals(sessionInfo.role())) {
                log.debug("[AuthService] Token is not from a guest, role={}", sessionInfo.role());
                return Optional.empty();
            }

            if (sessionInfo.tableSessionId() == null || sessionInfo.participantId() == null || sessionInfo.foodVenueId() == null) {
                log.warn("[AuthService] Incomplete guest session data in token");
                return Optional.empty();
            }
            return Optional.of(sessionInfo);

        } catch (Exception e) {
            log.error("[AuthService] Error extracting guest session from request", e);
            return Optional.empty();
        }
    }

    private String extractTokenFromRequest() {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private SessionInfo createSessionInfoFromActiveSession(TableSession tableSession, User loggedUser) {
        SessionInfo activeSession = SessionInfo.builder()
                .subject(loggedUser.getEmail())
                .foodVenueId(tableSession.getFoodVenue().getId())
                .participantId(findParticipantIdForUser(tableSession, loggedUser))
                .tableSessionId(tableSession.getId())
                .role(RoleType.ROLE_CLIENT.name())
                .build();
        log.debug("[AuthService] Recovering active TableSession of logged user. Session info={}", activeSession);
        return activeSession;
    }

    private UUID findParticipantIdForUser(TableSession tableSession, User loggedUser) {
        return tableSession.getParticipants().stream()
                .filter(participant -> loggedUser.getId().equals(participant.getUser().getId()))
                .map(Participant::getId)
                .findFirst()
                .orElse(null);
    }

    private SessionInfo promoteGuestSessionToClient(User loggedUser, SessionInfo guestSession) {

        Participant guestParticipant = participantService.update(guestSession.participantId(), loggedUser);

        log.debug("[AuthService] Promoted guest participant={} to user={}",
                guestParticipant.getNickname(), loggedUser.getEmail());

        // Crear nueva sesión info con datos actualizados
        SessionInfo promotedSession = SessionInfo.builder()
                .subject(loggedUser.getEmail())
                .foodVenueId(guestSession.foodVenueId())
                .participantId(guestSession.participantId())
                .tableSessionId(guestSession.tableSessionId())
                .role(RoleType.ROLE_CLIENT.name())
                .build();
        log.debug("[AuthService] Created session info for promoted client. Session info={}", promotedSession);
        return promotedSession;
    }

    private SessionInfo createSessionInfoForLoggedUser(User loggedUser) {
        return SessionInfo.builder()
                .foodVenueId(extractFoodVenueId())
                .participantId(getCurrentParticipant()
                        .map(Participant::getId)
                        .orElse(null))
                .tableSessionId(getCurrentTableSession()
                        .map(TableSession::getId)
                        .orElse(null))
                .role(RoleType.ROLE_CLIENT.name())
                .subject(loggedUser.getEmail())
                .build();
    }

    private UUID extractFoodVenueId() {
        return getCurrentTableSession()
                .map(TableSession::getFoodVenue)
                .map(FoodVenue::getId)
                .orElse(null);
    }

    private String createAccessToken(User user, SessionInfo sessionInfo) {
        return jwtService.generateAccessToken(user, sessionInfo);
    }

    private String createRefreshToken(User loggedUser) {
        return refreshTokenService.generateRefreshToken(loggedUser.getEmail());
    }

    private LoginResponse createLoginResponse(User loggedUser, String accessToken, String refreshToken) {
        Instant expiration = jwtService.getExpirationDateFromToken(accessToken);
        AuthResponse authResponse = new AuthResponse(accessToken, refreshToken, expiration);

        if (!loggedUser.getEmployments().isEmpty()) {
            log.debug("[AuthService] Authenticated user has role options available.");
            RoleSelectionResponseDto roleSelection = roleSelectionService.generateRoleSelection(loggedUser);
            roleSelection.setAuthResponse(authResponse);
            return roleSelection;
        }
        log.debug("[AuthService] Authenticated user with ROLE_CLIENT.");
        return authResponse;
    }

    public Optional<CustomUserPrincipal> getCurrentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserPrincipal principal)) {
            return Optional.empty();
        }
        return Optional.of(principal);
    }

}