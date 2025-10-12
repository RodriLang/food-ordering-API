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
import com.group_three.food_ordering.services.AdminService;
import com.group_three.food_ordering.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.group_three.food_ordering.utils.EntityName.ADMIN_EMPLOYMENT;
import static com.group_three.food_ordering.utils.EntityName.USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final EmploymentRepository employmentRepository;
    private final UserService userService;
    private final EmploymentMapper employmentMapper;
    private final TenantContext tenantContext;

    @Override
    public EmploymentResponseDto createAdminUser(EmploymentRequestDto dto) {
        User user = userService.getEntityByEmail(dto.getUserEmail());

        FoodVenue foodVenue = tenantContext.requireFoodVenue();

        Employment employment = Employment.builder()
                .publicId(UUID.randomUUID())
                .user(user)
                .foodVenue(foodVenue)
                .role(RoleType.ROLE_ADMIN)
                .build();

        log.debug("[EmploymentRepository] Calling save to create new ADMIN employment for user {} in venue {}",
                user.getPublicId(), foodVenue.getPublicId());

        Employment savedEmployment = employmentRepository.save(employment);
        return employmentMapper.toResponseDto(savedEmployment);
    }

    @Override
    public EmploymentResponseDto findByEmail(String email) {
        User user = userService.getEntityByEmail(email);

        log.debug("[EmploymentRepository] Calling findByUser_PublicId for userId={}", user.getPublicId());
        Employment employment = employmentRepository.findByUser_PublicId(user.getPublicId()).getFirst();
        return employmentMapper.toResponseDto(employment);
    }

    @Override
    public EmploymentResponseDto findById(UUID id) {
        Employment employment = getEmploymentByPublicId(id);
        return employmentMapper.toResponseDto(employment);
    }

    @Override
    public Page<EmploymentResponseDto> getActiveAdminUsers(Pageable pageable) {
        log.debug("[EmploymentRepository] Calling getAllByActiveAndRole for active ADMIN users");
        return employmentRepository.getAllByActiveAndRole(pageable, Boolean.TRUE, RoleType.ROLE_ADMIN)
                .map(employmentMapper::toResponseDto);
    }

    @Override
    public Page<EmploymentResponseDto> getInactiveAdminUsers(Pageable pageable) {
        log.debug("[EmploymentRepository] Calling getAllByActiveAndRole for inactive ADMIN users");
        return employmentRepository.getAllByActiveAndRole(pageable, Boolean.FALSE, RoleType.ROLE_ADMIN)
                .map(employmentMapper::toResponseDto);
    }

    @Override
    public Page<EmploymentResponseDto> getAllAdminUsers(Pageable pageable) {
        log.debug("[EmploymentRepository] Calling getAllByRole for all ADMIN users");
        return employmentRepository.getAllByRole(pageable, RoleType.ROLE_ADMIN)
                .map(employmentMapper::toResponseDto);
    }

    @Override
    public EmploymentResponseDto update(UUID publicId, EmploymentRequestDto dto) {
        Employment employment = getEmploymentByPublicId(publicId);
        employmentMapper.update(dto, employment);
        log.debug("[EmploymentRepository] Calling save to update ADMIN employment {}", publicId);
        Employment updatedEmployment = employmentRepository.save(employment);
        return employmentMapper.toResponseDto(updatedEmployment);
    }

    @Override
    public void deleteAdminUser(UUID publicId) {
        Employment employment = getEmploymentByPublicId(publicId);
        employment.setDeleted(Boolean.TRUE);
        log.debug("[EmploymentRepository] Calling save to soft delete ADMIN employment {}", publicId);
        employmentRepository.save(employment);
    }

    private Employment getEmploymentByPublicId(UUID publicId) {
        UUID foodVenueId = tenantContext.requireFoodVenue().getPublicId();
        log.debug("[EmploymentRepository] Calling findByPublicIdAndFoodVenueAndActive for ADMIN employment {}", publicId);
        return employmentRepository.findByPublicIdAndFoodVenue_PublicIdAndActive(publicId, foodVenueId, Boolean.TRUE)
                .orElseThrow(() -> new EntityNotFoundException(ADMIN_EMPLOYMENT, publicId.toString()));
    }
}
