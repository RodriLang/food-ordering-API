package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
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
import com.group_three.food_ordering.services.AuthService;
import com.group_three.food_ordering.services.RootService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RootServiceImpl implements RootService {

    private final EmploymentRepository employmentRepository;
    private final UserRepository userRepository;
    private final FoodVenueRepository foodVenueRepository;
    private final EmploymentMapper employmentMapper;
    private final AuthService authService;

    private static final String USER_ENTITY_NAME = "User";
    private static final String FOOD_VENUE_ENTITY_NAME = "FoodVenue";
    private static final String EMPLOYMENT_ENTITY_NAME = "Employment";


    @Override
    public EmploymentResponseDto createRootUser(EmploymentRequestDto dto) {

        User user = userRepository.findByEmail(dto.getUserEmail())
                .orElseThrow(() -> new EntityNotFoundException(USER_ENTITY_NAME));

        FoodVenue foodVenue = getFoodVenue(dto.getFoodVenueId());

        Employment employment = Employment.builder()
                .publicId(UUID.randomUUID())
                .user(user)
                .foodVenue(foodVenue)
                .role(RoleType.ROLE_ROOT)
                .build();

        Employment savedEmployment = employmentRepository.save(employment);

        return employmentMapper.toResponseDto(savedEmployment);
    }

    @Override
    public Page<EmploymentResponseDto> getRootUsers(Pageable pageable) {
        return employmentRepository.getAllByActiveAndRole(pageable, Boolean.TRUE, RoleType.ROLE_ROOT)
                .map(employmentMapper::toResponseDto);
    }

    @Override
    public EmploymentResponseDto selectContext(UUID foodVenuePublicId) {

        FoodVenue selectedFoodVenue = getFoodVenue(foodVenuePublicId);
        UUID authenticatedUserId = authService.determineAuthUser().getPublicId();

        Employment employment = employmentRepository.findByUser_PublicIdAndRoleAndActiveTrue(
                authenticatedUserId, RoleType.ROLE_ROOT).getFirst();

        if (employment == null) {
            throw new EntityNotFoundException(EMPLOYMENT_ENTITY_NAME);
        }
        employment.setFoodVenue(selectedFoodVenue);
        employmentRepository.save(employment);
        return employmentMapper.toResponseDto(employment);
    }

    private FoodVenue getFoodVenue(UUID foodVenueId) {
        return foodVenueRepository.findByPublicId(foodVenueId)
                .orElseThrow(() -> new EntityNotFoundException(FOOD_VENUE_ENTITY_NAME));
    }
}
