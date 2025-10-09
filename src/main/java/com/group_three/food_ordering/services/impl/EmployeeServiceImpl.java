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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.group_three.food_ordering.utils.EntityName.EMPLOYMENT;

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

        User user = userRepository.findByEmail(dto.getUserEmail())
                .orElseThrow(() -> new EntityNotFoundException(USER_ENTITY_NAME));

        FoodVenue foodVenue = tenantContext.getCurrentFoodVenue();
        RoleType role = dto.getRole();
        validateAllowedRole(role);

        Employment employment = Employment.builder()
                .user(user)
                .foodVenue(foodVenue)
                .role(role)
                .build();

        Employment savedEmployment = employmentRepository.save(employment);
        return employmentMapper.toResponseDto(savedEmployment);
    }

    @Override
    public Page<EmploymentResponseDto> getEmployeeUsers(Pageable pageable) {
        UUID foodVenueId = tenantContext.getCurrentFoodVenueId();

        return employmentRepository.getAllByActiveAndFoodVenue_PublicId(pageable, Boolean.TRUE, foodVenueId)
                .map(employmentMapper::toResponseDto);
    }

    @Override
    public Page<EmploymentResponseDto> getEmployeeUsers(Pageable pageable, RoleType role) {
        FoodVenue foodVenue = tenantContext.getCurrentFoodVenue();
        validateAllowedRole(role);
        return employmentRepository.getAllByActiveAndRoleAndFoodVenue_PublicId(
                        pageable, Boolean.TRUE, role, foodVenue.getPublicId())
                .map(employmentMapper::toResponseDto);
    }

    @Override
    public EmploymentResponseDto updateEmployee(UUID publicId, EmploymentRequestDto dto) {

        Employment employment = getEmploymentByPublicId(publicId);
        validateContext(employment);
        employmentMapper.update(dto, employment);
        Employment updatedEmployment = employmentRepository.save(employment);
        return employmentMapper.toResponseDto(updatedEmployment);
    }

    @Override
    public void deleteEmployeeUser(UUID publicId) {
        Employment employment = getEmploymentByPublicId(publicId);
        validateContext(employment);
        employmentRepository.delete(employment);
    }

    private Employment getEmploymentByPublicId(UUID publicId) {
        UUID foodVenueId = tenantContext.getCurrentFoodVenueId();
        return employmentRepository.findByPublicIdAndFoodVenue_PublicIdAndActive(publicId, foodVenueId, Boolean.TRUE)
                .orElseThrow(() -> new EntityNotFoundException(EMPLOYMENT, publicId.toString()));
    }

    private void validateContext(Employment employment) {
        UUID foodVenueId = tenantContext.getCurrentFoodVenueId();
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
