package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.RoleSelectionRequestDto;
import com.group_three.food_ordering.security.LoginResponse;
import com.group_three.food_ordering.services.RoleSelectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.ROLE_SELECTOR_URI)
@RequiredArgsConstructor
public class RoleSelectorController {

    private final RoleSelectionService roleSelectionService;


    @PostMapping("/select")
    public ResponseEntity<LoginResponse> select(@RequestBody RoleSelectionRequestDto request) {
        return ResponseEntity.ok(roleSelectionService.selectRole(request));
    }

    @PostMapping("/client")
    public ResponseEntity<LoginResponse> client() {
        return ResponseEntity.ok(roleSelectionService.selectClient());
    }
}