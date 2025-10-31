package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.models.Employment;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface EmploymentService {

    Employment getEmploymentEntityById(UUID publicId, Boolean active);

    EmploymentResponseDto create(FoodVenue foodVenue, User user, RoleType role);

    EmploymentResponseDto getEmploymentDtoById(UUID publicId);

    EmploymentResponseDto update(UUID publicId, Employment newEmployment);

    Page<Employment> findByFilters(UUID foodVenueId, List<RoleType> roles, Boolean active, Pageable pageable);

    List<Employment> getEmploymentsByUser(String userEmail, UUID foodVenueId, Boolean active);

    void softDelete(UUID publicId);
}