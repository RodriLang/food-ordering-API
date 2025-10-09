package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.RoleSelectionRequestDto;
import com.group_three.food_ordering.dto.response.RoleEmploymentResponseDto;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.dto.response.AuthResponse;

import java.util.List;

public interface RoleSelectionService {

    AuthResponse selectRole(RoleSelectionRequestDto request);

    AuthResponse selectClient();

    List<RoleEmploymentResponseDto> generateRoleSelection(User user);
}
