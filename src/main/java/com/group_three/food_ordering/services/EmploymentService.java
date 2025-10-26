package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.models.Employment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface EmploymentService {

    EmploymentResponseDto create(EmploymentRequestDto dto);

    Employment getEmploymentEntityById(UUID publicId, Boolean active);

    EmploymentResponseDto getEmploymentDtoById(UUID publicId);

    List<Employment> getEmploymentsByUser(String userEmail, UUID foodVenueId, Boolean active);

    EmploymentResponseDto update(UUID publicId, Employment newEmployment);

    void softDelete(UUID publicId);

    Page<EmploymentResponseDto> findByFilters(UUID foodVenueId, List<RoleType> roles, Boolean active, Pageable pageable);
}