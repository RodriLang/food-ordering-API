package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.RootController;
import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.dto.response.LoginResponse;
import com.group_three.food_ordering.services.RootService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class RootControllerImpl implements RootController {

    private final RootService rootService;

    @Override
    public ResponseEntity<Page<EmploymentResponseDto>> getAllRootUsers(Pageable pageable) {
        return ResponseEntity.ok((rootService.getRootUsers(pageable)));
    }

    @Override
    public ResponseEntity<EmploymentResponseDto> registerRootUser(EmploymentRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rootService.createRootUser(dto));
    }

    @Override
    public ResponseEntity<LoginResponse> selectContext(UUID foodVenueId) {
        return ResponseEntity.ok(rootService.selectContext(foodVenueId));
    }
}
