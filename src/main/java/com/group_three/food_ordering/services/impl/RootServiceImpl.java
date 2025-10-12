package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.SessionInfo;
import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.EmploymentMapper;
import com.group_three.food_ordering.models.Employment;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.repositories.EmploymentRepository;
import com.group_three.food_ordering.repositories.FoodVenueRepository;
import com.group_three.food_ordering.repositories.UserRepository;
import com.group_three.food_ordering.security.JwtService;
import com.group_three.food_ordering.services.RootService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.group_three.food_ordering.utils.EntityName.USER;
import static com.group_three.food_ordering.utils.EntityName.FOOD_VENUE;
import static com.group_three.food_ordering.utils.EntityName.EMPLOYMENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class RootServiceImpl implements RootService {

    private final EmploymentRepository employmentRepository;
    private final UserRepository userRepository;
    private final FoodVenueRepository foodVenueRepository;
    private final EmploymentMapper employmentMapper;
    private final TenantContext tenantContext;
    private final JwtService jwtService;


    @Override
    public EmploymentResponseDto createRootUser(EmploymentRequestDto dto) {
        log.debug("[UserRepository] Calling findByEmail for user email={}", dto.getUserEmail());
        User user = userRepository.findByEmail(dto.getUserEmail())
                .orElseThrow(() -> new EntityNotFoundException(USER));

        FoodVenue foodVenue = getFoodVenue(dto.getFoodVenueId());

        Employment employment = Employment.builder()
                .publicId(UUID.randomUUID())
                .user(user)
                .foodVenue(foodVenue)
                .role(RoleType.ROLE_ROOT)
                .build();

        log.debug("[EmploymentRepository] Calling save to create new ROOT employment for user {} in venue {}",
                user.getPublicId(), foodVenue.getPublicId());
        Employment savedEmployment = employmentRepository.save(employment);

        return employmentMapper.toResponseDto(savedEmployment);
    }

    @Override
    public Page<EmploymentResponseDto> getRootUsers(Pageable pageable) {
        log.debug("[EmploymentRepository] Calling getAllByActiveAndRole to retrieve active ROOT users");
        return employmentRepository.getAllByActiveAndRole(pageable, Boolean.TRUE, RoleType.ROLE_ROOT)
                .map(employmentMapper::toResponseDto);
    }

    @Override
    public AuthResponse selectContext(UUID foodVenuePublicId) {
        log.debug("[RootService] Select context process started");
        FoodVenue selectedFoodVenue = getFoodVenue(foodVenuePublicId);
        log.debug("[RootService] Selected FoodVenue foodVenueId={}", selectedFoodVenue.getPublicId());
        User authenticatedUser = tenantContext.requireUser();
        log.debug("[RootService] Authenticated user email={} publicId={}",
                authenticatedUser.getEmail(), authenticatedUser.getPublicId());

        log.debug("[EmploymentRepository] Calling findByUser_PublicIdAndRoleAndActiveTrue for user {} and role ROOT",
                authenticatedUser.getPublicId());
        Employment employment = employmentRepository.findByUser_PublicIdAndRoleAndActiveTrue(
                authenticatedUser.getPublicId(), RoleType.ROLE_ROOT).getFirst();

        if (employment == null) {
            throw new EntityNotFoundException(EMPLOYMENT);
        }
        employment.setFoodVenue(selectedFoodVenue);
        log.debug("[RootService] Context Selected foodVenueId={} role={}",
                employment.getFoodVenue().getPublicId(), employment.getRole());

        SessionInfo sessionInfo = SessionInfo.builder()
                .foodVenueId(selectedFoodVenue.getPublicId())
                .subject(authenticatedUser.getEmail())
                .role(employment.getRole().name())
                .userId(authenticatedUser.getPublicId())
                .build();

        String token = jwtService.generateAccessToken(sessionInfo);
        return AuthResponse.builder()
                .accessToken(token)
                .build();
    }

    private FoodVenue getFoodVenue(UUID foodVenueId) {
        log.debug("[FoodVenueRepository] Calling findByPublicId for foodVenueId={}", foodVenueId);
        return foodVenueRepository.findByPublicId(foodVenueId)
                .orElseThrow(() -> new EntityNotFoundException(FOOD_VENUE));
    }
}