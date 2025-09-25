package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.create.TableSessionCreateDto;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.dto.response.OrderResponseDto;
import com.group_three.food_ordering.dto.response.UserResponseDto;
import com.group_three.food_ordering.enums.OrderStatus;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping(ApiPaths.CURRENT_USER)
public interface ProfileController {


    @GetMapping("/user")
    ResponseEntity<UserResponseDto> getAuthenticatedUser();

    @GetMapping("/orders")
    ResponseEntity<Page<OrderResponseDto>> getMyOrders(
            @RequestParam(required = false) OrderStatus orderStatus,
            @Parameter(hidden = true) Pageable pageable);

    @PostMapping("/table-sessions")
    ResponseEntity<AuthResponse> createTableSession(@RequestBody @Valid TableSessionCreateDto tableSessionCreateDto);

    @GetMapping("/table-sessions")
    ResponseEntity<Page<OrderResponseDto>> getMyCurrentTableSessionOrders(
            @RequestParam(required = false) OrderStatus orderStatus,
            @Parameter(hidden = true) Pageable pageable);

}
