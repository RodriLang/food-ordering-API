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
import com.group_three.food_ordering.services.AdminService;
import com.group_three.food_ordering.services.EmploymentService;
import com.group_three.food_ordering.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.group_three.food_ordering.utils.EntityName.ADMIN_EMPLOYMENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final EmploymentService employmentService;
    private final UserService userService;
    private final EmploymentMapper employmentMapper;
    private final TenantContext tenantContext;

    @Override
    public EmploymentResponseDto createAdminUser(EmploymentRequestDto dto) {

        User user = userService.getEntityByEmail(dto.getUserEmail());
        FoodVenue foodVenue = tenantContext.requireFoodVenue();

        return employmentService.create(foodVenue, user, dto.getRole());
    }

    @Override
    public List<EmploymentResponseDto> findByEmail(String email) {
        UUID foodVenueId = tenantContext.getFoodVenueId();
        log.debug("[EmploymentRepository] Calling findByUser_PublicId for userId={}", email);
        return employmentService.getEmploymentsByUser(email, foodVenueId, Boolean.TRUE).stream()
                .map(employmentMapper::toResponseDto)
                .toList();
    }

    @Override
    public EmploymentResponseDto findById(UUID id) {
        Employment employment = getEmploymentByPublicId(id);
        return employmentMapper.toResponseDto(employment);
    }

    @Override
    public Page<EmploymentResponseDto> getActiveAdminUsers(Pageable pageable) {
        UUID foodVenueId = tenantContext.getFoodVenueId();
        log.debug("[EmploymentRepository] Calling getAllByActiveAndRole for active ADMIN users");
        return employmentService.findByFilters(foodVenueId, List.of(RoleType.ROLE_ADMIN), Boolean.TRUE, pageable)
                .map(employmentMapper::toResponseDto);
    }

    @Override
    public Page<EmploymentResponseDto> getInactiveAdminUsers(Pageable pageable) {
        UUID foodVenueId = tenantContext.getFoodVenueId();
        log.debug("[EmploymentRepository] Calling getAllByActiveAndRole for inactive ADMIN users");
        return employmentService.findByFilters(foodVenueId, List.of(RoleType.ROLE_ADMIN), Boolean.FALSE, pageable)
                .map(employmentMapper::toResponseDto);
    }

    @Override
    public Page<EmploymentResponseDto> getAllAdminUsers(Pageable pageable) {
        UUID foodVenueId = tenantContext.getFoodVenueId();
        log.debug("[EmploymentRepository] Calling getAllByRole for all ADMIN users");
        return employmentService.findByFilters(foodVenueId, List.of(RoleType.ROLE_ADMIN), Boolean.FALSE, pageable)
                .map(employmentMapper::toResponseDto);
    }

    @Override
    public EmploymentResponseDto update(UUID publicId, EmploymentRequestDto dto) {
        Employment employment = getEmploymentByPublicId(publicId);
        employmentMapper.update(dto, employment);
        log.debug("[EmploymentRepository] Calling save to update ADMIN employment {}", publicId);
        return employmentService.update(publicId, employment);
    }

    @Override
    public void deleteAdminUser(UUID publicId) {
        Employment employment = getEmploymentByPublicId(publicId);
        employment.setDeleted(Boolean.TRUE);
        log.debug("[EmploymentRepository] Calling save to soft delete ADMIN employment {}", publicId);
        employmentService.update(publicId, employment);
    }

    private Employment getEmploymentByPublicId(UUID publicId) {
        UUID foodVenueId = tenantContext.getFoodVenueId();
        log.debug("[EmploymentRepository] Calling findByPublicIdAndFoodVenueAndActive for ADMIN employment {}", publicId);
        Employment employment = employmentService.getEmploymentEntityByIdAndActive(publicId, Boolean.TRUE);

        if (!employment.getFoodVenue().getPublicId().equals(foodVenueId)) {
            throw new EntityNotFoundException(ADMIN_EMPLOYMENT, publicId.toString());
        }
        return employment;
    }
}
