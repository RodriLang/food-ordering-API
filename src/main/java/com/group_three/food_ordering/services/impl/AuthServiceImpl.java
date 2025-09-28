package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.SessionInfo;
import com.group_three.food_ordering.dto.request.LoginRequest;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.dto.response.RoleSelectionResponseDto;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.models.*;
import com.group_three.food_ordering.repositories.*;
import com.group_three.food_ordering.security.CustomUserPrincipal;
import com.group_three.food_ordering.security.JwtService;
import com.group_three.food_ordering.dto.response.LoginResponse;
import com.group_three.food_ordering.services.AuthService;
import com.group_three.food_ordering.services.RoleSelectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final TableSessionRepository tableSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleSelectionService roleSelectionService;


    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User loggedUser = authenticateUser(loginRequest);
        SessionInfo sessionInfo = resolveSessionInfo(loggedUser);
        String token = createAuthToken(loggedUser, sessionInfo);

        return createLoginResponse(loggedUser, token);
    }

    @Override
    public Optional<User> getCurrentUser() {
        return getPrincipalResource(
                "user",
                CustomUserPrincipal::getEmail,
                email -> userRepository.findByEmail((String) email)
        );
    }

    @Override
    public Optional<Participant> getCurrentParticipant() {
        return getPrincipalResource(
                "participant",
                CustomUserPrincipal::getParticipantId,
                id -> participantRepository.findById((UUID) id)
        );
    }

    @Override
    public Optional<TableSession> getCurrentTableSession() {
        return getPrincipalResource(
                "table session",
                CustomUserPrincipal::getTableSessionId,
                id -> tableSessionRepository.findById((UUID) id)
        );
    }


    private User authenticateUser(LoginRequest loginRequest) {
        User loggedUser = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), loggedUser.getPassword())) {
            throw new BadCredentialsException("Usuario o contraseña incorrectos");
        }
        return loggedUser;
    }

    private SessionInfo resolveSessionInfo(User loggedUser) {
        log.debug("[AuthService] Verifying active table session.");
        return tableSessionRepository.findActiveSessionByUserId(loggedUser.getEmail())
                .map(tableSession -> createSessionInfoFromActiveSession(tableSession, loggedUser))
                .orElseGet(this::createSessionInfoFromCurrentGuestSession);
    }

    private SessionInfo createSessionInfoFromActiveSession(TableSession tableSession, User loggedUser) {
        log.debug("[AuthService] Authenticated user has an active table session. They will be redirected.");
        return new SessionInfo(
                tableSession.getFoodVenue().getId(),
                findParticipantIdForUser(tableSession, loggedUser),
                tableSession.getId()
        );
    }

    private UUID findParticipantIdForUser(TableSession tableSession, User loggedUser) {
        return tableSession.getParticipants().stream()
                .filter(participant -> loggedUser.getId().equals(participant.getUser().getId()))
                .map(Participant::getId)
                .findFirst()
                .orElse(null);
    }


    private SessionInfo createSessionInfoFromCurrentGuestSession() {
        log.debug("[AuthService] Create Session info from current Guest Session.");
        return new SessionInfo(
                extractFoodVenueId(),
                getCurrentParticipant()
                        .map(Participant::getId)
                        .orElse(null),
                getCurrentTableSession()
                        .map(TableSession::getId)
                        .orElse(null)
        );
    }

    private UUID extractFoodVenueId() {
        return getCurrentTableSession()
                .map(TableSession::getFoodVenue)
                .map(FoodVenue::getId)
                .orElse(null);
    }

    private String createAuthToken(User loggedUser, SessionInfo sessionInfo) {
        return jwtService.generateToken(
                loggedUser.getEmail(),
                sessionInfo.foodVenueId(),
                RoleType.ROLE_CLIENT.name(),
                sessionInfo.tableSessionId(),
                sessionInfo.participantId()
        );
    }

    private LoginResponse createLoginResponse(User loggedUser, String token) {
        if (!loggedUser.getEmployments().isEmpty()) {
            log.debug("[AuthService] Authenticated user has role options available.");
            RoleSelectionResponseDto roleSelection = roleSelectionService.generateRoleSelection(loggedUser);
            roleSelection.setToken(token);
            return roleSelection;
        }

        return new AuthResponse(token);
    }

    private <T> Optional<T> getPrincipalResource(
            String resourceName,
            java.util.function.Function<CustomUserPrincipal, Object> principalExtractor,
            java.util.function.Function<Object, Optional<T>> resourceFetcher) {

        log.debug("[AuthService] Getting current {} from principal", resourceName);
        CustomUserPrincipal principal = getPrincipal();

        if (principal == null) {
            log.debug("[AuthService] No authenticated principal");
            return Optional.empty();
        }

        Object resourceId = principalExtractor.apply(principal);
        log.debug("[AuthService] Current {}={}", resourceName, resourceId);

        return Optional.ofNullable(resourceId)
                .flatMap(resourceFetcher);
    }

    private CustomUserPrincipal getPrincipal() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserPrincipal customUserPrincipal) {
            return customUserPrincipal;
        } else {
            return null;
        }
    }
}