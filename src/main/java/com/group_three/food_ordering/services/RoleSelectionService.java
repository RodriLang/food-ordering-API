package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.RoleSelectionRequestDto;
import com.group_three.food_ordering.dto.response.RoleEmploymentResponseDto;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.dto.response.LoginResponse;

import java.util.List;

public interface RoleSelectionService {

    LoginResponse selectRole(RoleSelectionRequestDto request);

    LoginResponse selectClient();

    List<RoleEmploymentResponseDto> generateRoleSelection(User user);
}
