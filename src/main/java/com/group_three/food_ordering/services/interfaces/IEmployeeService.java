package com.group_three.food_ordering.services.interfaces;

import com.group_three.food_ordering.dtos.create.EmployeeCreateDto;
import com.group_three.food_ordering.dtos.response.EmployeeResponseDto;
import com.group_three.food_ordering.dtos.update.EmployeePatchDto;
import com.group_three.food_ordering.dtos.update.EmployeeUpdateDto;
import com.group_three.food_ordering.models.Employee;

import java.util.List;
import java.util.UUID;

public interface IEmployeeService {

    EmployeeResponseDto create(EmployeeCreateDto dto);

    List<EmployeeResponseDto> getAll();

    EmployeeResponseDto getById(UUID id);

    void delete(UUID id);

    EmployeeResponseDto update(UUID id, EmployeeUpdateDto dto);

    Employee getEntityById(UUID id);

    EmployeeResponseDto replace(UUID id, EmployeeUpdateDto dto);

    EmployeeResponseDto partialUpdate(UUID id, EmployeePatchDto dto);
}
