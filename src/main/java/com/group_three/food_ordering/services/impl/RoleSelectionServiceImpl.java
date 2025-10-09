package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.SessionInfo;
import com.group_three.food_ordering.dto.request.RoleSelectionRequestDto;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.dto.response.RoleEmploymentResponseDto;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.models.Employment;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.repositories.UserRepository;
import com.group_three.food_ordering.security.CustomUserPrincipal;
import com.group_three.food_ordering.security.JwtService;
import com.group_three.food_ordering.dto.response.LoginResponse;
import com.group_three.food_ordering.services.EmploymentService;
import com.group_three.food_ordering.security.RefreshTokenService;
import com.group_three.food_ordering.services.RoleSelectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleSelectionServiceImpl implements RoleSelectionService {

    private final EmploymentService employmentService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    @Override
    public LoginResponse selectRole(RoleSelectionRequestDto request) {
        User authenticatedUser = getAuthenticatedUser();
        Employment employment = employmentService.getEntityByIdAndActiveTrue(request.employmentId());
        log.debug("[RoleSelection] Role selected={}", employment.getRole());
        return generateLoginResponse(authenticatedUser, employment.getFoodVenue().getPublicId(), employment.getRole().name());
    }

    @Override
    public LoginResponse selectClient() {
        User authenticatedUser = getAuthenticatedUser();
        log.debug("[RoleSelection] Employment selected ROLE_CLIENT");

        return generateLoginResponse(authenticatedUser, null, RoleType.ROLE_CLIENT.name());
    }

    @Override
    public List<RoleEmploymentResponseDto> generateRoleSelection(User user) {
        log.info("[RoleSelection] Searching roles");
        List<RoleEmploymentResponseDto> roleEmployments = employmentService.getRoleEmploymentsByUserAndActiveTrue(user.getPublicId());
        if (!roleEmployments.isEmpty()) {
            log.info("[RoleSelection] Role selection generated for user {}", user.getEmail());
        }
        return roleEmployments;
    }

    private LoginResponse generateLoginResponse(User user, UUID foodVenueId, String role) {
        String accessToken = jwtService.generateAccessToken(SessionInfo.builder()
                .userId(user.getPublicId())
                .subject(user.getEmail())
                .foodVenueId(foodVenueId)
                .role(role)
                .build());

        List<RoleEmploymentResponseDto> roleSelection = generateRoleSelection(user);
        String refreshToken = refreshTokenService.generateRefreshToken(user.getEmail());
        Instant expiration = jwtService.getExpirationDateFromToken(refreshToken);
        AuthResponse authResponse = new AuthResponse(accessToken, refreshToken, expiration);
        return LoginResponse.builder()
                .authResponse(authResponse)
                .employments(roleSelection).build();
    }

    private User getAuthenticatedUser() {
        log.debug("[RoleSelectionService] Fetching authenticated user from security context");
        CustomUserPrincipal principal = (CustomUserPrincipal)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.debug("[RoleSelectionService] Authenticated user email: {}", principal.getEmail());
        return userRepository.findByEmail(principal.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User"));
    }
}