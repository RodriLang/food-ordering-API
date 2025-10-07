package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.RootUserController;
import com.group_three.food_ordering.dto.response.UserResponseDto;
import com.group_three.food_ordering.dto.request.UserRequestDto;
import com.group_three.food_ordering.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@PreAuthorize("hasRole('ROOT')")
@RestController
@RequiredArgsConstructor
public class RootUserControllerImpl implements RootUserController {

    private final UserService userService;
    private final PageableHandlerMethodArgumentResolverCustomizer pageableCustomizer;

    @Override
    public ResponseEntity<UserResponseDto> getById(UUID id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @Override
    public ResponseEntity<Page<UserResponseDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(userService.getAll(pageable));
    }

    @Override
    public ResponseEntity<Page<UserResponseDto>> getActives(Pageable pageable) {
        return ResponseEntity.ok(userService.getActiveUsers(pageable));
    }

    @Override
    public ResponseEntity<Page<UserResponseDto>> getDeleted(Pageable pageable) {
        return ResponseEntity.ok(userService.getDeletedUsers(pageable));
    }

    @Override
    public ResponseEntity<UserResponseDto> updateById(
            UUID id,
            UserRequestDto dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @Override
    public ResponseEntity<UserResponseDto> patchUserById(
            UUID id,
            UserRequestDto dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @Override
    public ResponseEntity<Void> deleteById(UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
