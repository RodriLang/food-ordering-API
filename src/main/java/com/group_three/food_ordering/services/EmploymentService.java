package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.dto.response.RoleEmploymentResponseDto;
import com.group_three.food_ordering.models.Employment;

import java.util.List;
import java.util.UUID;

public interface EmploymentService {

    EmploymentResponseDto create(EmploymentRequestDto dto);

    List<RoleEmploymentResponseDto> getRoleEmploymentsByUser(UUID userId);

    List<EmploymentResponseDto> getAll();

    List<EmploymentResponseDto> getByUser(UUID userId);

    EmploymentResponseDto getById(UUID id);

    void delete(UUID id);

    EmploymentResponseDto update(UUID id, EmploymentRequestDto dto);

    Employment getEntityById(UUID id);

}
