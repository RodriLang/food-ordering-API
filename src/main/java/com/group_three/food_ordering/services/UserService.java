package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.UserRequestDto;
import com.group_three.food_ordering.dto.response.UserDetailResponseDto;
import com.group_three.food_ordering.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    UserDetailResponseDto create(UserRequestDto dto);

    UserDetailResponseDto getById(UUID id);

    Page<UserDetailResponseDto> getAll(Pageable pageable);

    Page<UserDetailResponseDto> getActiveUsers(Pageable pageable);

    Page<UserDetailResponseDto> getDeletedUsers(Pageable pageable);

    UserDetailResponseDto updateUser(UUID id, UserRequestDto dto);

    UserDetailResponseDto updateAuthUser(UserRequestDto dto);

    void deleteUser(UUID id);

    void deleteAuthUser();

    User getEntityById(UUID id);

    User getEntityByEmail(String email);

    UserDetailResponseDto getAuthenticatedUser();
}
