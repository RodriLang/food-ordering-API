package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AdminService {

    EmploymentResponseDto createAdminUser(EmploymentRequestDto dto);

    List<EmploymentResponseDto> findByEmail(String email);

    EmploymentResponseDto findById(UUID id);

    Page<EmploymentResponseDto> getActiveAdminUsers(Pageable pageable);

    Page<EmploymentResponseDto> getInactiveAdminUsers(Pageable pageable);

    Page<EmploymentResponseDto> getAllAdminUsers(Pageable pageable);

    EmploymentResponseDto update(UUID publicId, EmploymentRequestDto dto);

    void deleteAdminUser(UUID publicId);

}
