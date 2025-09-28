package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.RootUserController;
import com.group_three.food_ordering.dto.response.UserResponseDto;
import com.group_three.food_ordering.dto.update.UserUpdateDto;
import com.group_three.food_ordering.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@PreAuthorize("hasRole('ROOT')")
@RestController
@RequiredArgsConstructor
public class RootUserControllerImpl implements RootUserController {

    private final UserService userService;

    @Override
    public ResponseEntity<UserResponseDto> getById(UUID id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @Override
    public ResponseEntity<List<UserResponseDto>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @Override
    public ResponseEntity<List<UserResponseDto>> getActives() {
        return ResponseEntity.ok(userService.getActiveUsers());
    }

    @Override
    public ResponseEntity<List<UserResponseDto>> getDeleted() {
        return ResponseEntity.ok(userService.getDeletedUsers());
    }

    @Override
    public ResponseEntity<UserResponseDto> updateById(
            UUID id,
            UserUpdateDto dto) {
        return ResponseEntity.ok(userService.update(id, dto));
    }

    @Override
    public ResponseEntity<UserResponseDto> patchUserById(
            UUID id,
            UserUpdateDto dto) {
        return ResponseEntity.ok(userService.update(id, dto));
    }

    @Override
    public ResponseEntity<Void> deleteById(UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
