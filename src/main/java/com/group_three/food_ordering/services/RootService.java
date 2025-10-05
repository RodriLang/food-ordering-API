package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RootService {

    EmploymentResponseDto createRootUser(EmploymentRequestDto dto);

    Page<EmploymentResponseDto> getRootUsers(Pageable pageable);

    EmploymentResponseDto selectContext(UUID foodVenuePublicId);

}
