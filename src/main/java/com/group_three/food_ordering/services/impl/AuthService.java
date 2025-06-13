package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dtos.create.LoginRequest;
import com.group_three.food_ordering.dtos.response.AuthResponse;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.models.*;
import com.group_three.food_ordering.repositories.*;
import com.group_three.food_ordering.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final IUserRepository userRepository;
    private final IEmployeeRepository employeeRepository;
    private final IClientRepository clientRepository;
    private final ITableSessionRepository tableSessionRepository;
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

    public String getCurrentEmail() {
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
        return SecurityContextHolder.getContext().getAuthentication().getName();

    }

    public User getCurrentUser() {
        return userRepository.findByEmail(getCurrentEmail())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    public Client getCurrentClient() {
        return clientRepository.findByUser_Email(getCurrentEmail())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
    }

    public TableSession getCurrentTableSession() {
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
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalStateException("Token JWT no presente o mal formado");
        }
        return authHeader.substring(7);}
}