package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.enums.RoleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface EmployeeService {

    EmploymentResponseDto createEmployeeUser(EmploymentRequestDto dto);

    Page<EmploymentResponseDto> getEmployeeUsers(Pageable pageable);

    Page<EmploymentResponseDto> getEmployeeUsers(Pageable pageable, RoleType role);

    EmploymentResponseDto updateEmployee(UUID publicId, EmploymentRequestDto dto);

    void deleteEmployeeUser(UUID publicId);

}
