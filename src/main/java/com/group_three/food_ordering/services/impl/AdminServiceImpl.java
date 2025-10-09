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
import com.group_three.food_ordering.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.group_three.food_ordering.utils.EntityName.ADMIN_EMPLOYMENT;
import static com.group_three.food_ordering.utils.EntityName.USER;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final EmploymentRepository employmentRepository;
    private final UserRepository userRepository;
    private final EmploymentMapper employmentMapper;
    private final TenantContext tenantContext;

    @Override
    public EmploymentResponseDto createAdminUser(EmploymentRequestDto dto) {

        User user = userRepository.findByEmail(dto.getUserEmail())
                .orElseThrow(() -> new EntityNotFoundException(USER));

        FoodVenue foodVenue = tenantContext.getCurrentFoodVenue();

        Employment employment = Employment.builder()
                .publicId(UUID.randomUUID())
                .user(user)
                .foodVenue(foodVenue)
                .role(RoleType.ROLE_ADMIN)
                .build();

        Employment savedEmployment = employmentRepository.save(employment);
        return employmentMapper.toResponseDto(savedEmployment);
    }

    @Override
    public EmploymentResponseDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(USER, email));
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
        return employmentRepository.getAllByActiveAndRole(pageable, Boolean.TRUE, RoleType.ROLE_ADMIN)
                .map(employmentMapper::toResponseDto);
    }

    @Override
    public Page<EmploymentResponseDto> getInactiveAdminUsers(Pageable pageable) {
        return employmentRepository.getAllByActiveAndRole(pageable, Boolean.FALSE, RoleType.ROLE_ADMIN)
                .map(employmentMapper::toResponseDto);
    }

    @Override
    public Page<EmploymentResponseDto> getAllAdminUsers(Pageable pageable) {
        return employmentRepository.getAllByRole(pageable, RoleType.ROLE_ADMIN)
                .map(employmentMapper::toResponseDto);
    }

    @Override
    public EmploymentResponseDto update(UUID publicId, EmploymentRequestDto dto) {
        Employment employment = getEmploymentByPublicId(publicId);
        employmentMapper.update(dto, employment);
        Employment updatedEmployment = employmentRepository.save(employment);
        return employmentMapper.toResponseDto(updatedEmployment);
    }

    @Override
    public void deleteAdminUser(UUID publicId) {
        Employment employment = getEmploymentByPublicId(publicId);
        employmentRepository.delete(employment);
    }

    private Employment getEmploymentByPublicId(UUID publicId) {
        UUID foodVenueId = tenantContext.getCurrentFoodVenue().getPublicId();
        return employmentRepository.findByPublicIdAndFoodVenue_PublicIdAndActive(publicId, foodVenueId, Boolean.TRUE)
                .orElseThrow(() -> new EntityNotFoundException(ADMIN_EMPLOYMENT, publicId.toString()));
    }
}
