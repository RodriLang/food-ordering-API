package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.request.LoginRequest;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.models.*;
import com.group_three.food_ordering.repositories.*;
import com.group_three.food_ordering.security.JwtService;
import com.group_three.food_ordering.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
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
    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;
    private final TableSessionRepository tableSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final HttpServletRequest request;

    public AuthResponse login(LoginRequest loginRequest) {
        Optional<Employee> employeeOpt = employeeRepository.findByUser_Email(loginRequest.getEmail());

        if (employeeOpt.isPresent()) {
            Employee emp = employeeOpt.get();

            if (passwordEncoder.matches(loginRequest.getPassword(), emp.getUser().getPassword())) {
                String token = jwtService.generateToken(
                        loginRequest.getEmail(),
                        emp.getFoodVenue().getId(),
                        emp.getUser().getRole().name(),
                        null,
                        null);
                return new AuthResponse(token);
            }
        }

        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                String token = jwtService.generateToken(
                        loginRequest.getEmail(),
                        null,
                        user.getRole().name(),
                        null,
                        null);
                return new AuthResponse(token);
            }
        }

        throw new BadCredentialsException("Usuario o contraseña incorrectos");
    }

    @Override
    public AuthResponse initTableSession(User user, UUID foodVenueId, UUID tableSessionId) {

        return AuthResponse.builder()
                    .token(jwtService.generateToken(user.getEmail(),
                            foodVenueId,
                            user.getRole().name(),
                            tableSessionId,
                            user.getId()))
                    .build();
    }

    public String getCurrentEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();

    }

    public User getCurrentUser() {
        return userRepository.findByEmail(getCurrentEmail())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    public Client getCurrentClient() {
        log.debug("[AuthService] Obteniendo Cliente autenticado.");
        return clientRepository.findByUser_Email(getCurrentEmail())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
    }

    public TableSession getCurrentTableSession() {
        log.debug("[AuthService] Obteniendo Sesión de mesa actual.");
        String token = extractTokenFromRequest();
        String sessionIdString = jwtService.getClaim(token, claims -> claims.get("tableSessionId", String.class));

        if (sessionIdString == null) {
            throw new IllegalStateException("El token no contiene tableSessionId");
        }

        UUID sessionId = UUID.fromString(sessionIdString);
        return tableSessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("TableSession no encontrada"));
    }

    private String extractTokenFromRequest() {
        final String authHeader = request.getHeader("Authorization");
        log.info("::: Obteniendo token :::");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("::: Token invalido {} :::", authHeader);
            throw new IllegalStateException("Token JWT no presente o mal formado");
        }
        return authHeader.substring(7);
    }
}