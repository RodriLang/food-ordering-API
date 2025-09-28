package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.RoleSelectionRequestDto;
import com.group_three.food_ordering.dto.response.RoleSelectionResponseDto;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.security.LoginResponse;

public interface RoleSelectionService {

    LoginResponse selectRole(RoleSelectionRequestDto request);

    LoginResponse selectClient();

    RoleSelectionResponseDto generateRoleSelection(User user);
}
