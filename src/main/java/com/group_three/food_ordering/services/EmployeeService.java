package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.EmployeeRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface EmployeeService {

    EmploymentResponseDto createEmployeeUser(EmployeeRequestDto dto);

    EmploymentResponseDto updateEmployee(UUID publicId, EmployeeRequestDto dto);

    EmploymentResponseDto getEmploymentById(UUID publicId);

    void deleteEmployeeUser(UUID publicId);

    Page<EmploymentResponseDto> getFilteredEmployments(String email, Boolean active, Pageable pageable);

    Boolean existsUserByEmail(String email);
}