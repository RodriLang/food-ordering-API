package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AdminService {

    EmploymentResponseDto createAdminUser(EmploymentRequestDto dto);

    EmploymentResponseDto findByEmail(String email);

    EmploymentResponseDto findById(UUID id);

    Page<EmploymentResponseDto> getAdminUsers(Pageable pageable);

    EmploymentResponseDto update(UUID publicId, EmploymentRequestDto dto);

    void deleteAdminUser(UUID publicId);

}
