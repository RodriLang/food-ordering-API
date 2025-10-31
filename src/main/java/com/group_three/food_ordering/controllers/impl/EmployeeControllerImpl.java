package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.EmployeeController;
import com.group_three.food_ordering.dto.request.EmployeeRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.dto.response.PageResponse;
import com.group_three.food_ordering.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class EmployeeControllerImpl implements EmployeeController {

    private final EmployeeService employeeService;


    @Override
    public ResponseEntity<EmploymentResponseDto> create(EmployeeRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createEmployeeUser(dto));
    }

    @Override
    public ResponseEntity<EmploymentResponseDto> update(
            UUID id,
            EmployeeRequestDto dto) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, dto));
    }

    @Override
    public ResponseEntity<EmploymentResponseDto> getById(UUID id) {
        return ResponseEntity.ok(employeeService.getEmploymentById(id));
    }

    @Override
    public ResponseEntity<Void> delete(UUID id) {
        employeeService.deleteEmployeeUser(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<PageResponse<EmploymentResponseDto>> getEmployees(String email, Boolean active, Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(employeeService.getFilteredEmployments(email, active, pageable)));
    }

    @Override
    public ResponseEntity<Boolean> existsByEmail(String email) {
        return ResponseEntity.ok(employeeService.existsUserByEmail(email));
    }
}
