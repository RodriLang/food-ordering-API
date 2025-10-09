package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.SessionInfo;
import com.group_three.food_ordering.dto.request.LoginRequest;
import com.group_three.food_ordering.dto.request.RefreshTokenRequest;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.exceptions.InvalidTokenException;
import com.group_three.food_ordering.dto.AuditorUser;
import com.group_three.food_ordering.models.*;
import com.group_three.food_ordering.repositories.*;
import com.group_three.food_ordering.security.CustomUserPrincipal;
import com.group_three.food_ordering.security.JwtService;
import com.group_three.food_ordering.dto.response.LoginResponse;
import com.group_three.food_ordering.services.AuthService;
import com.group_three.food_ordering.security.RefreshTokenService;
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

import static com.group_three.food_ordering.utils.EntityName.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final TableSessionRepository tableSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final HttpServletRequest request;
    private final RoleSelectionService roleSelectionService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public AuditorUser getAuditorUser() {
        log.debug("[AuthService] Getting current auditor user");
        return getCurrentPrincipal()
                .map(principal -> {
                    UUID userId = principal.getUserId();
                    String email = principal.getEmail();
                    log.debug("[AuthService] Current auditor user id={}, email={}", userId, email);
                    return new AuditorUser(userId, email);
                })
                .orElseThrow(() -> new EntityNotFoundException(AUDITOR_USER));
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        log.debug("[AuthService] Login request received");
        User loggedUser = authenticateUser(loginRequest);
        log.debug("[AuthService] Logged in user={}", loggedUser.getEmail());
        SessionInfo sessionInfo = resolveSessionInfo(loggedUser);
        String accessToken = jwtService.generateAccessToken(sessionInfo);
        String refreshToken = refreshTokenService.generateRefreshToken(loggedUser.getEmail());
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
                .orElseThrow(() -> new UsernameNotFoundException(USER));

        SessionInfo sessionInfo = resolveSessionInfo(user);

        String newAccessToken = jwtService.generateAccessToken(sessionInfo);

        String newRefreshToken = refreshTokenService.generateRefreshToken(userEmail);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    public Optional<User> getAuthUser() {
        log.debug("[AuthService] Getting auth user from principal");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }
        log.debug("[AuthService] Auth user={}", auth.getPrincipal());
        return getCurrentPrincipal()
                .map(CustomUserPrincipal::getEmail)
                .flatMap(userRepository::findByEmail);
    }

    @Override
    public Optional<Participant> getCurrentParticipant() {
        log.debug("[AuthService] Getting current participant from principal");
        return getCurrentPrincipal()
                .map(CustomUserPrincipal::getParticipantId)
                .flatMap(participantRepository::findByPublicId);
    }

    @Override
    public Optional<TableSession> getCurrentTableSession() {
        log.debug("[AuthService] Getting current table session from principal");
        Optional<CustomUserPrincipal> principal = getCurrentPrincipal();

        if (principal.isPresent()) {
            log.debug("[AuthService] Principal found");
            UUID tableSessionId = principal.get().getTableSessionId();
            log.debug("[AuthService] TableSession found in principal tableSessionId={}", tableSessionId);
            return tableSessionRepository.findByPublicId(tableSessionId);
        }
        log.debug("[AuthService] Principal not found");
        return Optional.empty();
    }

    @Override
    public User determineAuthUser() {
        return getAuthUser().orElseThrow(() -> new EntityNotFoundException(USER));
    }

    @Override
    public Participant determineCurrentParticipant() {
        return getCurrentParticipant().orElseThrow(() -> new EntityNotFoundException(PARTICIPANT));
    }

    @Override
    public TableSession determineCurrentTableSession() {
        return getCurrentTableSession().orElseThrow(() -> new EntityNotFoundException(TABLE_SESSION));
    }

    @Override
    public RoleType getCurrentParticipantRole() {
        return getCurrentParticipant()
                .map(Participant::getRole)
                .orElse(null);
    }

    private User authenticateUser(LoginRequest loginRequest) {
        log.debug("[AuthService] Authenticating user email={}", loginRequest.getEmail());
        Optional<User> loggedUser = userRepository.findByEmail(loginRequest.getEmail());
        log.debug("[AuthService] Logged in user=");
        if (loggedUser.isEmpty()) {
            log.warn("[AuthController] User not found for email={}", loginRequest.getEmail());
            throw new UsernameNotFoundException("Usuario o contraseña incorrectos");
        }
        User user = loggedUser.get();
        log.debug("[AuthService] Authenticating user={}", user.getEmail());
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.warn("[AuthController] User authentication failed for email={}", loginRequest.getEmail());
            throw new BadCredentialsException("Usuario o contraseña incorrectos");
        }
        log.debug("[AuthService] Authenticated user: {}", user.getEmail());
        return user;
    }

    private SessionInfo resolveSessionInfo(User loggedUser) {
        log.debug("[AuthService] Resolving session info for user={}", loggedUser.getEmail());

        // 1. Usuario registrado con sesión activa en DB
        Optional<TableSession> previousActiveSession =
                tableSessionRepository.findActiveSessionByUserEmailAndDeletedFalse(loggedUser.getEmail());
        if (previousActiveSession.isPresent()) {
            log.debug("[AuthService] Found active session={} for user={}",
                    previousActiveSession.get().getPublicId(), loggedUser.getEmail());
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
        log.debug("[AuthService] Creating session info from active TableSession id={} for user={}",
                tableSession.getPublicId(), loggedUser.getPublicId());

        SessionInfo activeSession = SessionInfo.builder()
                .userId(loggedUser.getPublicId())
                .subject(loggedUser.getEmail())
                .foodVenueId(tableSession.getFoodVenue().getPublicId())
                .participantId(findParticipantIdForUser(tableSession, loggedUser))
                .tableSessionId(tableSession.getPublicId())
                .role(RoleType.ROLE_CLIENT.name())
                .build();
        log.debug("[AuthService] Recovering active TableSession of logged user. Session info={}", activeSession);
        return activeSession;
    }

    private UUID findParticipantIdForUser(TableSession tableSession, User loggedUser) {
        return tableSession.getParticipants().stream()
                .filter(participant -> loggedUser.getEmail().equals(participant.getUser().getEmail()))
                .map(Participant::getPublicId)
                .findFirst()
                .orElse(null);
    }

    private SessionInfo promoteGuestSessionToClient(User loggedUser, SessionInfo guestSession) {

        Participant guestParticipant = updateParticipant(guestSession.participantId(), loggedUser);

        log.debug("[AuthService] Promoted guest participant={} to user={}",
                guestParticipant.getNickname(), loggedUser.getEmail());

        // Crear nueva sesión info con datos actualizados
        SessionInfo promotedSession = SessionInfo.builder()
                .userId(loggedUser.getPublicId())
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
        log.debug("[AuthService] Creating clean session info for logged user={} id={}",
                loggedUser.getEmail(), loggedUser.getPublicId());

        return SessionInfo.builder()
                .userId(loggedUser.getPublicId())
                .foodVenueId(extractFoodVenueId())
                .participantId(getCurrentParticipant()
                        .map(Participant::getPublicId)
                        .orElse(null))
                .tableSessionId(getCurrentTableSession()
                        .map(TableSession::getPublicId)
                        .orElse(null))
                .role(RoleType.ROLE_CLIENT.name())
                .subject(loggedUser.getEmail())
                .build();
    }

    private UUID extractFoodVenueId() {
        return getCurrentTableSession()
                .map(TableSession::getFoodVenue)
                .map(FoodVenue::getPublicId)
                .orElse(null);
    }

    private LoginResponse createLoginResponse(User loggedUser, String accessToken, String refreshToken) {
        log.debug("[AuthService] Generating login response");
        Instant expiration = jwtService.getExpirationDateFromToken(accessToken);
        AuthResponse authResponse = new AuthResponse(accessToken, refreshToken, expiration);
        log.debug("[AuthService] Auth response generated");
        return LoginResponse.builder()
                .authResponse(authResponse)
                .employments(roleSelectionService.generateRoleSelection(loggedUser))
                .build();
    }

    public Optional<CustomUserPrincipal> getCurrentPrincipal() {
        log.debug("[AuthService] Getting current principal from SecurityContext");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.debug("[AuthService] Authentication found");
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserPrincipal principal)) {
            log.debug("[AuthService] CustomUserPrincipal not found in authentication");
            return Optional.empty();
        }
        log.debug("[AuthService] Return CustomUserPrincipal Optional");
        return Optional.of(principal);
    }

    private Participant updateParticipant(UUID participantIdUser, User user) {
        Participant participant = participantRepository.findByPublicId(participantIdUser)
                .orElseThrow(()-> new EntityNotFoundException(PARTICIPANT));

            participant.setUser(user);
            participant.setRole(RoleType.ROLE_CLIENT);
            participant.setNickname(user.getName());

        participantRepository.save(participant);
        log.debug("[ParticipantService] Participant updated. Nickname={}. Role={}. User={}",
                participant.getNickname(), participant.getRole(), user.getEmail());
        return participant;
    }

}