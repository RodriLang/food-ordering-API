package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.EmployeeController;
import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.dto.response.PageResponse;
import com.group_three.food_ordering.services.EmploymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
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
    public ResponseEntity<PageResponse<EmploymentResponseDto>> getEmploymentsByUser(
            String email, Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(employmentService.getByUserAndActiveTrue(email, pageable)));
    }

    @Override
    public ResponseEntity<Void> delete(UUID id) {
        employmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<PageResponse<EmploymentResponseDto>> getActiveEmployees(Pageable pageable) {

        return ResponseEntity.ok(PageResponse.of(employmentService.getAllAndActiveTrue(pageable)));
    }

    @Override
    public ResponseEntity<PageResponse<EmploymentResponseDto>> getDeletedEmployees(Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(employmentService.getAllAndActiveFalse(pageable)));
    }
}
