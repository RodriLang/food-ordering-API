package com.group_three.food_ordering.services.interfaces;

import com.group_three.food_ordering.dtos.create.EmployeeCreateDto;
import com.group_three.food_ordering.dtos.response.EmployeeResponseDto;
import com.group_three.food_ordering.dtos.update.EmployeeUpdateDto;

import java.util.List;
import java.util.UUID;

public interface IEmployeeService {

    EmployeeResponseDto create(EmployeeCreateDto dto);

    EmployeeResponseDto update(UUID id, EmployeeUpdateDto dto);

    EmployeeResponseDto getById(UUID id);

    List<EmployeeResponseDto> getAll();
}
