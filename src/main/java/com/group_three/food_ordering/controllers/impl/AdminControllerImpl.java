package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.AdminController;
import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@PreAuthorize("hasRole('ROLE_ROOT')")
@RestController
@RequiredArgsConstructor
public class AdminControllerImpl implements AdminController {

    private final AdminService adminService;

    @Override
    public ResponseEntity<EmploymentResponseDto> registerAdmin(EmploymentRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createAdminUser(dto));
    }

    @Override
    public ResponseEntity<EmploymentResponseDto> getById(UUID id) {
        return ResponseEntity.ok(adminService.findById(id));
    }

    @Override
    public ResponseEntity<EmploymentResponseDto> getByEmail(String email) {
        return ResponseEntity.ok(adminService.findByEmail(email));
    }

    @Override
    public ResponseEntity<Page<EmploymentResponseDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllAdminUsers(pageable));
    }

    @Override
    public ResponseEntity<Page<EmploymentResponseDto>> getActives(Pageable pageable) {
        return ResponseEntity.ok(adminService.getActiveAdminUsers(pageable));
    }

    @Override
    public ResponseEntity<Page<EmploymentResponseDto>> getInactives(Pageable pageable) {
        return ResponseEntity.ok(adminService.getInactiveAdminUsers(pageable));
    }

    @Override
    public ResponseEntity<EmploymentResponseDto> update(UUID id, EmploymentRequestDto dto) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteById(UUID id) {
        adminService.deleteAdminUser(id);
        return ResponseEntity.noContent().build();
    }
}
