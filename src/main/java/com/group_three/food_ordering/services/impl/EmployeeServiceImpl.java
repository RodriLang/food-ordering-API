package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.EmploymentMapper;
import com.group_three.food_ordering.models.Employment;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.repositories.EmploymentRepository;
import com.group_three.food_ordering.repositories.UserRepository;
import com.group_three.food_ordering.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.group_three.food_ordering.utils.EntityName.EMPLOYMENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmploymentRepository employmentRepository;
    private final UserRepository userRepository;
    private final EmploymentMapper employmentMapper;
    private final TenantContext tenantContext;

    private static final String USER_ENTITY_NAME = "User";

    @Override
    public EmploymentResponseDto createEmployeeUser(EmploymentRequestDto dto) {
        log.debug("[UserRepository] Calling findByEmail for user email={}", dto.getUserEmail());
        User user = userRepository.findByEmailAndDeletedFalse(dto.getUserEmail())
                .orElseThrow(() -> new EntityNotFoundException(USER_ENTITY_NAME));

        FoodVenue foodVenue = tenantContext.requireFoodVenue();
        RoleType role = dto.getRole();
        validateAllowedRole(role);

        Employment employment = Employment.builder()
                .user(user)
                .foodVenue(foodVenue)
                .role(role)
                .build();

        log.debug("[EmploymentRepository] Calling save to create new employment for user {} in venue {}",
                user.getPublicId(), foodVenue.getPublicId());

        Employment savedEmployment = employmentRepository.save(employment);
        return employmentMapper.toResponseDto(savedEmployment);
    }

    @Override
    public Page<EmploymentResponseDto> getEmployeeUsers(Pageable pageable) {
        UUID foodVenueId = tenantContext.getFoodVenueId();
        log.debug("[EmploymentRepository] Calling getAllByActiveAndFoodVenue_PublicId for active employees in venue {}",
                foodVenueId);

        return employmentRepository.getAllByActiveAndFoodVenue_PublicIdAndDeletedFalse(pageable, Boolean.TRUE, foodVenueId)
                .map(employmentMapper::toResponseDto);
    }

    @Override
    public Page<EmploymentResponseDto> getEmployeeUsers(Pageable pageable, RoleType role) {
        FoodVenue foodVenue = tenantContext.requireFoodVenue();
        validateAllowedRole(role);
        log.debug("[EmploymentRepository] Calling getAllByActiveAndRoleAndFoodVenue_PublicId for role {} in venue {}",
                role, foodVenue.getPublicId());
        return employmentRepository.getAllByActiveAndRoleAndFoodVenue_PublicIdAndDeletedFalse(
                        pageable, Boolean.TRUE, role, foodVenue.getPublicId())
                .map(employmentMapper::toResponseDto);
    }

    @Override
    public EmploymentResponseDto updateEmployee(UUID publicId, EmploymentRequestDto dto) {

        Employment employment = getEmploymentByPublicId(publicId);
        validateContext(employment);
        employmentMapper.update(dto, employment);
        log.debug("[EmploymentRepository] Calling save to update employment {}", publicId);
        Employment updatedEmployment = employmentRepository.save(employment);
        return employmentMapper.toResponseDto(updatedEmployment);
    }

    @Override
    public void deleteEmployeeUser(UUID publicId) {
        Employment employment = getEmploymentByPublicId(publicId);
        validateContext(employment);
        employment.setDeleted(Boolean.TRUE);
        log.debug("[EmploymentRepository] Calling save to soft delete employment {}", publicId);
        employmentRepository.save(employment);
    }

    private Employment getEmploymentByPublicId(UUID publicId) {
        UUID foodVenueId = tenantContext.getFoodVenueId();
        log.debug("[EmploymentRepository] Calling findByPublicIdAndFoodVenue_PublicIdAndActive for " +
                "employment {} in venue {}", publicId, foodVenueId);

        return employmentRepository.findByPublicIdAndFoodVenue_PublicIdAndActiveAndDeletedFalse(publicId, foodVenueId, Boolean.TRUE)
                .orElseThrow(() -> new EntityNotFoundException(EMPLOYMENT, publicId.toString()));
    }

    private void validateContext(Employment employment) {
        UUID foodVenueId = tenantContext.getFoodVenueId();
        if (!foodVenueId.equals(employment.getFoodVenue().getPublicId())) {
            throw new EntityNotFoundException(EMPLOYMENT);
        }
    }

    private void validateAllowedRole(RoleType role) {
        if (!role.equals(RoleType.ROLE_STAFF) && !role.equals(RoleType.ROLE_MANAGER)) {
            throw new IllegalArgumentException("Invalid role for employee. Only ROLE_STAFF or ROLE_MANAGER are allowed.");
        }
    }
}
