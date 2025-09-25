package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.create.EmployeeCreateDto;
import com.group_three.food_ordering.dto.response.EmployeeResponseDto;
import com.group_three.food_ordering.dto.update.EmployeePatchDto;
import com.group_three.food_ordering.dto.update.EmployeeUpdateDto;
import com.group_three.food_ordering.models.Employee;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {

    EmployeeResponseDto create(EmployeeCreateDto dto);

    List<EmployeeResponseDto> getAll();

    EmployeeResponseDto getById(UUID id);

    void delete(UUID id);

    EmployeeResponseDto update(UUID id, EmployeeUpdateDto dto);

    Employee getEntityById(UUID id);

    EmployeeResponseDto replace(UUID id, EmployeeUpdateDto dto);

    EmployeeResponseDto partialUpdate(UUID id, EmployeePatchDto dto);
}
