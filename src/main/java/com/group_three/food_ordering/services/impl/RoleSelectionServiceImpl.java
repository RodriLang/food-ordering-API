package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.request.RoleSelectionRequestDto;
import com.group_three.food_ordering.dto.response.RoleEmploymentResponseDto;
import com.group_three.food_ordering.dto.response.RoleSelectionResponseDto;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.models.Employment;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.repositories.UserRepository;
import com.group_three.food_ordering.security.CustomUserPrincipal;
import com.group_three.food_ordering.security.JwtService;
import com.group_three.food_ordering.dto.response.LoginResponse;
import com.group_three.food_ordering.services.EmploymentService;
import com.group_three.food_ordering.services.RoleSelectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleSelectionServiceImpl implements RoleSelectionService {

    private final EmploymentService employmentService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public LoginResponse selectRole(RoleSelectionRequestDto request) {
        User authenticatedUser = getAuthenticatedUser();
        Employment employment = employmentService.getEntityByIdAndActiveTrue(request.employmentId());
        log.debug("[RoleSelection] Role selected={}", employment.getRole());
        return generateLoginResponse(authenticatedUser, employment.getFoodVenue().getId(), employment.getRole().name());
    }

    @Override
    public LoginResponse selectClient() {
        User authenticatedUser = getAuthenticatedUser();
        log.debug("[RoleSelection] Employment selected ROLE_CLIENT");
        return generateLoginResponse(authenticatedUser, null, RoleType.ROLE_CLIENT.name());
    }

    @Override
    public RoleSelectionResponseDto generateRoleSelection(User user) {
        List<RoleEmploymentResponseDto> roleEmployments = employmentService.getRoleEmploymentsByUserAndActiveTrue(user.getId());
        RoleSelectionResponseDto roleSelection = RoleSelectionResponseDto.builder()
                .employments(roleEmployments)
                .build();
        log.info("[RoleSelection] Role selection generated for user {}", user.getEmail());
        return roleSelection;
    }

    private LoginResponse generateLoginResponse(User user, UUID foodVenueId, String role) {
        String token = jwtService.generateToken(
                user.getEmail(),
                foodVenueId,
                role,
                null,
                null
        );
        RoleSelectionResponseDto roleSelection = generateRoleSelection(user);
        roleSelection.setToken(token);

        return roleSelection;
    }

    private User getAuthenticatedUser() {
        CustomUserPrincipal principal = (CustomUserPrincipal)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userRepository.findByEmail(principal.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User"));
    }
}