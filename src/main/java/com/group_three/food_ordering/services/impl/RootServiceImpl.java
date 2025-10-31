package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.SessionInfo;
import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.mappers.EmploymentMapper;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.configs.security.JwtService;
import com.group_three.food_ordering.services.EmploymentService;
import com.group_three.food_ordering.services.FoodVenueService;
import com.group_three.food_ordering.services.RootService;
import com.group_three.food_ordering.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RootServiceImpl implements RootService {

    private final EmploymentService employmentService; // Servicio Central
    private final FoodVenueService foodVenueService;
    private final UserService userService;
    private final TenantContext tenantContext;
    private final JwtService jwtService;
    private final EmploymentMapper employmentMapper;

    @Override
    public EmploymentResponseDto createRootUser(EmploymentRequestDto dto) {

        log.info("Creating a new ROOT user for email {}", dto.getUserEmail());
        FoodVenue foodVenue = foodVenueService.findEntityById(dto.getFoodVenueId());
        User user = userService.getEntityByEmail(dto.getUserEmail());

        return employmentService.create(foodVenue, user, RoleType.ROLE_ROOT);
    }

    @Override
    public Page<EmploymentResponseDto> getRootUsers(Pageable pageable) {
        log.debug("Fetching all active ROOT users.");
        // Un usuario ROOT no está atado a un FoodVenue, por eso pasamos null
        return employmentService.findByFilters(null, List.of(RoleType.ROLE_ROOT), Boolean.TRUE, pageable)
                .map(employmentMapper::toResponseDto);
    }

    @Override
    public AuthResponse selectContext(UUID foodVenuePublicId) {
        log.info("ROOT user selecting context for food venue {}", foodVenuePublicId);

        FoodVenue selectedFoodVenue = foodVenueService.findEntityById(foodVenuePublicId);

        User authenticatedUser = tenantContext.requireUser();

        SessionInfo sessionInfo = SessionInfo.builder()
                .foodVenueId(selectedFoodVenue.getPublicId())
                .subject(authenticatedUser.getEmail())
                .role(RoleType.ROLE_ROOT.name()) // El rol no cambia
                .userId(authenticatedUser.getPublicId())
                .build();

        String token = jwtService.generateAccessToken(sessionInfo);
        Instant expiration = jwtService.getExpirationDateFromToken(token);

        return AuthResponse.builder()
                .accessToken(token)
                .expirationDate(expiration)
                .role(RoleType.ROLE_ROOT.name())
                .build();
    }
}