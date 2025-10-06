package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.EmployeeController;
import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.services.EmploymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ROOT')")
@RequiredArgsConstructor
public class EmployeeControllerImpl implements EmployeeController {

    private final EmploymentService employmentService;


    @Override
    public ResponseEntity<EmploymentResponseDto> create(EmploymentRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employmentService.createEmployment(dto));
    }

    @Override
    public ResponseEntity<EmploymentResponseDto> update(
            UUID id,
            EmploymentRequestDto dto) {
        return ResponseEntity.ok(employmentService.update(id, dto));
    }

    @Override
    public ResponseEntity<EmploymentResponseDto> getById(UUID id) {
        return ResponseEntity.ok(employmentService.getByIdAndActiveTrue(id));
    }

    @Override
    public ResponseEntity<Page<EmploymentResponseDto>> getEmploymentsByUser(
            String email, Pageable pageable) {
        return ResponseEntity.ok(employmentService.getByUserAndActiveTrue(email, pageable));
    }

    @Override
    public ResponseEntity<Void> delete(UUID id) {
        employmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Page<EmploymentResponseDto>> getActiveEmployees(Pageable pageable) {

        return ResponseEntity.ok(employmentService.getAllAndActiveTrue(pageable));
    }

    @Override
    public ResponseEntity<Page<EmploymentResponseDto>> getDeletedEmployees(Pageable pageable) {
        return ResponseEntity.ok(employmentService.getAllAndActiveFalse(pageable));
    }
}
