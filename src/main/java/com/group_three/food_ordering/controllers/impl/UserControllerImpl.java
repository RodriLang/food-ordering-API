package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.UserController;
import com.group_three.food_ordering.dto.create.UserCreateDto;
import com.group_three.food_ordering.dto.response.UserResponseDto;
import com.group_three.food_ordering.dto.update.UserUpdateDto;
import com.group_three.food_ordering.services.UserService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @PermitAll
    public ResponseEntity<UserResponseDto> register(
            UserCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(dto));
    }

    @Override
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<UserResponseDto> getById(UUID id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @Override
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<List<UserResponseDto>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @Override
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<List<UserResponseDto>> getActives() {
        return ResponseEntity.ok(userService.getActiveUsers());
    }

    @Override
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<List<UserResponseDto>> getDeleted() {
        return ResponseEntity.ok(userService.getDeletedUsers());
    }

    @Override
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<UserResponseDto> updateById(
            UUID id,
            UserUpdateDto dto) {
        return ResponseEntity.ok(userService.update(id, dto));
    }

    @Override
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<UserResponseDto> patchUserById(
            UUID id,
            UserUpdateDto dto) {
        return ResponseEntity.ok(userService.update(id, dto));
    }

    @Override
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<Void> deleteById(UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
