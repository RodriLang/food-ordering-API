package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.dto.response.RoleEmploymentResponseDto;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.EmploymentMapper;
import com.group_three.food_ordering.mappers.RoleEmploymentMapper;
import com.group_three.food_ordering.models.Employment;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.repositories.EmploymentRepository;
import com.group_three.food_ordering.repositories.UserRepository;
import com.group_three.food_ordering.services.EmploymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.group_three.food_ordering.utils.EntityName.EMPLOYMENT;
import static com.group_three.food_ordering.utils.EntityName.USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmploymentServiceImpl implements EmploymentService {

    private final EmploymentRepository employmentRepository;
    private final EmploymentMapper employmentMapper;
    private final RoleEmploymentMapper roleEmploymentMapper;
    private final TenantContext tenantContext;
    private final UserRepository userRepository;

    @Override
    public EmploymentResponseDto createEmployment(EmploymentRequestDto dto) {
        FoodVenue currentFoodVenue = tenantContext.requireFoodVenue();

        User employeeUser = getUserByEmail(dto.getUserEmail());

        Employment employment = Employment.builder()
                .publicId(UUID.randomUUID())
                .user(employeeUser)
                .foodVenue(currentFoodVenue)
                .role(dto.getRole())
                .build();

        log.debug("[EmploymentRepository] Calling save to create new employment for user {} in venue {}",
                employeeUser.getPublicId(), currentFoodVenue.getPublicId());
        employmentRepository.save(employment);
        return employmentMapper.toResponseDto(employment);
    }


    @Override
    public List<RoleEmploymentResponseDto> getRoleEmploymentsByUserAndActiveTrue(UUID userId) {
        log.debug("[EmploymentService] Getting active roles by user={}", userId);
        log.debug("[EmploymentRepository] Calling findByUser_PublicId (List) for userId={}", userId);
        List<Employment> employments = employmentRepository.findByUser_PublicIdAndDeletedFalseAndDeletedFalse(userId);
        employments.forEach(employment -> log.debug("[EmploymentService] Employments founded " +
                "FoodVenue={} Role={}", employment.getFoodVenue().getName(), employment.getRole()));

        return employments.stream()
                .map(roleEmploymentMapper::toResponseDto)
                .toList();
    }

    @Override
    public Page<EmploymentResponseDto> getAllAndActiveTrue(Pageable pageable) {
        log.debug("[EmploymentRepository] Calling getAllByActive to retrieve active employments");
        return employmentRepository.getAllByActiveAndDeletedFalse(pageable, Boolean.TRUE).map(employmentMapper::toResponseDto);
    }

    @Override
    public Page<EmploymentResponseDto> getAllAndActiveFalse(Pageable pageable) {
        log.debug("[EmploymentRepository] Calling getAllByActive to retrieve inactive employments");
        return employmentRepository.getAllByActiveAndDeletedFalse(pageable, Boolean.FALSE).map(employmentMapper::toResponseDto);
    }

    @Override
    public Page<EmploymentResponseDto> getByUserAndActiveTrue(String email, Pageable pageable) {

        User user = getUserByEmail(email);

        log.debug("[EmploymentRepository] Calling findByUser_PublicId for userId={}", user.getPublicId());
        return employmentRepository.findByUser_PublicIdAndDeletedFalseAndDeletedFalse(user.getPublicId(), pageable).map(employmentMapper::toResponseDto);
    }

    @Override
    public EmploymentResponseDto getByIdAndActiveTrue(UUID id) {
        Employment employment = getEntityByIdAndActiveTrue(id);
        return employmentMapper.toResponseDto(employment);
    }

    @Override
    public void delete(UUID id) {
        Employment employment = getEntityByIdAndActiveTrue(id);
        employment.setDeleted(Boolean.TRUE);
        log.debug("[EmploymentRepository] Calling save to soft delete employment {}", id);
        employmentRepository.save(employment);
    }

    @Override
    public EmploymentResponseDto update(UUID id, EmploymentRequestDto dto) {
        return null;
    }

    @Override
    public Employment getEntityByIdAndActiveTrue(UUID id) {
        log.debug("[EmploymentRepository] Calling findByPublicIdAndActive for employmentId={}", id);
        return employmentRepository.findByPublicIdAndActiveAndDeletedFalse(id, Boolean.TRUE)
                .orElseThrow(() -> new EntityNotFoundException(EMPLOYMENT));
    }

    private User getUserByEmail(String email) {
        log.debug("[UserRepository] Calling findByEmail for user email={}", email);
        return userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new EntityNotFoundException(USER));

    }
}