package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.RootUserController;
import com.group_three.food_ordering.dto.response.PageResponse;
import com.group_three.food_ordering.dto.response.UserDetailResponseDto;
import com.group_three.food_ordering.dto.request.UserRequestDto;
import com.group_three.food_ordering.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class RootUserControllerImpl implements RootUserController {

    private final UserService userService;

    @Override
    public ResponseEntity<UserDetailResponseDto> getById(UUID id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @Override
    public ResponseEntity<PageResponse<UserDetailResponseDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(userService.getAll(pageable)));
    }

    @Override
    public ResponseEntity<PageResponse<UserDetailResponseDto>> getActives(Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(userService.getActiveUsers(pageable)));
    }

    @Override
    public ResponseEntity<PageResponse<UserDetailResponseDto>> getDeleted(Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(userService.getDeletedUsers(pageable)));
    }

    @Override
    public ResponseEntity<UserDetailResponseDto> updateById(
            UUID id,
            UserRequestDto dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @Override
    public ResponseEntity<UserDetailResponseDto> patchUserById(
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
