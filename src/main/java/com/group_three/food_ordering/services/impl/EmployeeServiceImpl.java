package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.request.EmployeeRequestDto;
import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.models.Employment;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.services.EmployeeService;
import com.group_three.food_ordering.services.EmploymentService;
import com.group_three.food_ordering.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmploymentService employmentService;
    private final TenantContext tenantContext;
    private final UserService userService;

    @Override
    public EmploymentResponseDto createEmployeeUser(EmployeeRequestDto dto) {
        log.info("Creating a new employment with role {} for user {}", dto.getRole(), dto.getUserEmail());
        validateAllowedRole(dto.getRole());

        UUID foodVenueId = tenantContext.getFoodVenueId();

        EmploymentRequestDto employmentDto = EmploymentRequestDto.builder()
                .foodVenueId(foodVenueId)
                .role(dto.getRole())
                .userEmail(dto.getUserEmail())
                .build();

        // Delegación al servicio central
        return employmentService.create(employmentDto);
    }

    @Override
    public EmploymentResponseDto updateEmployee(UUID publicId, EmployeeRequestDto dto) {
        log.info("Updating employee with id {}", publicId);
        validateAllowedRole(dto.getRole());
        User user = userService.getEntityByEmail(dto.getUserEmail());
        FoodVenue foodVenue = tenantContext.requireFoodVenue();
        Employment employmentToUpdate = Employment.builder()
                .role(dto.getRole())
                .user(user)
                .foodVenue(foodVenue)
                .build();
        return employmentService.update(publicId, employmentToUpdate);
    }

    @Override
    public EmploymentResponseDto getEmploymentById(UUID publicId) {
        log.debug("Fetching employee with id {}", publicId);
        return employmentService.getEmploymentDtoById(publicId);
    }

    @Override
    public void deleteEmployeeUser(UUID publicId) {
        log.info("Soft-deleting employee with id {}", publicId);
        employmentService.softDelete(publicId);
    }

    @Override
    public Page<EmploymentResponseDto> getFilteredEmployments(String email, Boolean active, Pageable pageable) {
        UUID foodVenueId = tenantContext.getFoodVenueId();
        List<RoleType> allowedRoles = List.of(RoleType.ROLE_STAFF, RoleType.ROLE_MANAGER);

        log.debug("Fetching filtered employees for food venue {}", foodVenueId);

        return employmentService.findByFilters(foodVenueId, allowedRoles, active, pageable);
    }

    private void validateAllowedRole(RoleType role) {
        if (!role.equals(RoleType.ROLE_STAFF) && !role.equals(RoleType.ROLE_MANAGER)) {
            throw new IllegalArgumentException("Invalid role for employee. Only ROLE_STAFF or ROLE_MANAGER are allowed.");
        }
    }
}
