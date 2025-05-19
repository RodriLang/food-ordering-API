package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dtos.create.EmployeeCreateDto;
import com.group_three.food_ordering.dtos.response.EmployeeResponseDto;
import com.group_three.food_ordering.dtos.update.EmployeeUpdateDto;
import com.group_three.food_ordering.services.interfaces.IEmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.EMPLOYEE_BASE)
@RequiredArgsConstructor
public class EmployeeController {

    private final IEmployeeService employeeService;

    @PostMapping
    public ResponseEntity<EmployeeResponseDto> create(@RequestBody @Valid EmployeeCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> update(
            @PathVariable UUID id,
            @RequestBody @Valid EmployeeUpdateDto dto) {
        return ResponseEntity.ok(employeeService.update(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDto>> getAll() {
        return ResponseEntity.ok(employeeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeService.getById(id));
    }
}
