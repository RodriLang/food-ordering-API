package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.UserRequestDto;
import com.group_three.food_ordering.dto.response.UserResponseDto;
import com.group_three.food_ordering.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    UserResponseDto create(UserRequestDto dto);

    UserResponseDto getById(UUID id);

    Page<UserResponseDto> getAll(Pageable pageable);

    Page<UserResponseDto> getActiveUsers(Pageable pageable);

    Page<UserResponseDto> getDeletedUsers(Pageable pageable);

    UserResponseDto update(UUID id, com.group_three.food_ordering.dto.request.UserRequestDto dto);

    void delete(UUID id);

    User getEntityById(UUID id);

    UserResponseDto getAuthenticatedUser();
}
