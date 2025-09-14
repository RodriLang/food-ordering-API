package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.UserCreateDto;
import com.group_three.food_ordering.dto.update.UserUpdateDto;
import com.group_three.food_ordering.dto.response.UserResponseDto;
import com.group_three.food_ordering.models.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponseDto create(UserCreateDto dto);

    UserResponseDto getById(UUID id);

    List<UserResponseDto> getAll();

    List<UserResponseDto> getActiveUsers();

    List<UserResponseDto> getDeletedUsers();

    UserResponseDto update(UUID id, UserUpdateDto dto);

    void delete(UUID id);

    User getEntityById(UUID id);

    User createIfPresent(UserCreateDto dto);
}
