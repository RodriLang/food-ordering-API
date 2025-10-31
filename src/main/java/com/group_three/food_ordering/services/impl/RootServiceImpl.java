package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.SessionInfo;
import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.repositories.FoodVenueRepository;
import com.group_three.food_ordering.configs.security.JwtService;
import com.group_three.food_ordering.services.EmploymentService; // Servicio Central
import com.group_three.food_ordering.services.RootService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.group_three.food_ordering.utils.EntityName.FOOD_VENUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class RootServiceImpl implements RootService {

    private final EmploymentService employmentService; // Servicio Central
    private final FoodVenueRepository foodVenueRepository;
    private final TenantContext tenantContext;
    private final JwtService jwtService;

    @Override
    public EmploymentResponseDto createRootUser(EmploymentRequestDto dto) {
        log.info("Creating a new ROOT user for email {}", dto.getUserEmail());
        dto.setRole(RoleType.ROLE_ROOT); // Forzamos el rol
        return employmentService.create(dto);
    }

    @Override
    public Page<EmploymentResponseDto> getRootUsers(Pageable pageable) {
        log.debug("Fetching all active ROOT users.");
        // Un usuario ROOT no está atado a un FoodVenue, por eso pasamos null
        return employmentService.findByFilters(null, List.of(RoleType.ROLE_ROOT), Boolean.TRUE, pageable);
    }

    @Override
    public AuthResponse selectContext(UUID foodVenuePublicId) {
        log.info("ROOT user selecting context for food venue {}", foodVenuePublicId);

        // Esta lógica es única de RootService y no se puede delegar
        FoodVenue selectedFoodVenue = foodVenueRepository.findByPublicIdAndDeletedFalse(foodVenuePublicId)
                .orElseThrow(() -> new EntityNotFoundException(FOOD_VENUE, foodVenuePublicId.toString()));

        User authenticatedUser = tenantContext.requireUser();

        // Construimos la información para el nuevo token
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