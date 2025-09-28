package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.EmployeeRequestDto;
import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.dto.response.RoleEmploymentResponseDto;
import com.group_three.food_ordering.models.Employment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface EmploymentService {

    EmploymentResponseDto createEmployment(EmployeeRequestDto dto);

    List<RoleEmploymentResponseDto> getRoleEmploymentsByUserAndActiveTrue(UUID userId);

    Page<EmploymentResponseDto> getAllAndActiveTrue(Pageable pageable);

    Page<EmploymentResponseDto> getAllAndActiveFalse(Pageable pageable);

    Page<EmploymentResponseDto> getByUserAndActiveTrue(String email, Pageable pageable);

    EmploymentResponseDto getByIdAndActiveTrue(UUID id);

    void delete(UUID id);

    EmploymentResponseDto update(UUID id, EmploymentRequestDto dto);

    Employment getEntityByIdAndActiveTrue(UUID id);

}
