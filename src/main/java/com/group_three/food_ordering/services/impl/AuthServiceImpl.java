package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.request.LoginRequest;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.dto.response.RoleSelectionResponseDto;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.models.*;
import com.group_three.food_ordering.repositories.*;
import com.group_three.food_ordering.security.CustomUserPrincipal;
import com.group_three.food_ordering.security.JwtService;
import com.group_three.food_ordering.security.LoginResponse;
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

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User"));

        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            String token = jwtService.generateToken(
                    user.getEmail(),
                    null,
                    RoleType.ROLE_CLIENT.name(),
                    null,
                    null
            );
            if (!user.getEmployments().isEmpty()) {
                RoleSelectionResponseDto responseDto = roleSelectionService.generateRoleSelection(user);
                responseDto.setToken(token);
                return responseDto;
            }
            return new AuthResponse(token);
        }
        throw new BadCredentialsException("Usuario o contraseña incorrectos");
    }

    @Override
    public Optional<User> getCurrentUser() {
        log.debug("[AuthService] Getting current user from principal");
        CustomUserPrincipal principal = getPrincipal();

        if (principal == null) {
            log.debug("[AuthService] Unregistered user. Continue as GUEST_ROLE");
            return Optional.empty();
        }
        log.debug("[AuthService] Authenticated User email={}", principal.getEmail());
        return userRepository.findByEmail(principal.getEmail());
    }

    @Override
    public Optional<Participant> getCurrentParticipant() {
        log.debug("[AuthService] Getting current participant from principal");
        CustomUserPrincipal principal = getPrincipal();
        assert principal != null;
        log.debug("[AuthService] participant subject={}", principal.getEmail());
        return Optional.ofNullable(principal.getParticipantId())
                .flatMap(participantRepository::findById);
    }

    @Override
    public TableSession getCurrentTableSession() {
        CustomUserPrincipal principal = getPrincipal();
        assert principal != null;
        UUID sessionId = principal.getTableSessionId();
        if (sessionId == null) {
            throw new IllegalStateException("El token no contiene tableSessionId");
        }
        return tableSessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("TableSession no encontrada"));
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