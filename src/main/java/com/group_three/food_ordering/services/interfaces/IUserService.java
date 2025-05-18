package com.group_three.food_ordering.services.interfaces;

import com.group_three.food_ordering.dtos.create.UserCreateDto;
import com.group_three.food_ordering.dtos.update.UserUpdateDto;
import com.group_three.food_ordering.dtos.response.UserResponseDto;

import java.util.List;
import java.util.UUID;

public interface IUserService {

    UserResponseDto create(UserCreateDto dto);

    UserResponseDto getById(UUID id);

    List<UserResponseDto> getAll();

    List<UserResponseDto> getActiveUsers();

    List<UserResponseDto> getDeletedUsers();

    UserResponseDto update(UUID id, UserUpdateDto dto);

    void delete(UUID id);
}