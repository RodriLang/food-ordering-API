package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.RoleSelectorController;
import com.group_three.food_ordering.dto.request.RoleSelectionRequestDto;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.services.RoleSelectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RoleSelectorControllerImpl implements RoleSelectorController {

    private final RoleSelectionService roleSelectionService;


    @Override
    public ResponseEntity<AuthResponse> select(RoleSelectionRequestDto request) {
        log.debug("[RoleSelectorController] Request select role");
        return ResponseEntity.ok(roleSelectionService.selectRole(request));
    }

    @Override
    public ResponseEntity<AuthResponse> client() {
        log.debug("[RoleSelectorController] Request use client role");
        return ResponseEntity.ok(roleSelectionService.selectClient());

    }
}
