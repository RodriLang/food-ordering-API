package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.ProfileController;
import com.group_three.food_ordering.dto.create.TableSessionCreateDto;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.dto.response.OrderResponseDto;
import com.group_three.food_ordering.dto.response.TableSessionResponseDto;
import com.group_three.food_ordering.dto.response.UserResponseDto;
import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.services.OrderService;
import com.group_three.food_ordering.services.TableSessionService;
import com.group_three.food_ordering.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@PreAuthorize("hasRole('CLIENT')")
@RestController
@RequiredArgsConstructor
public class ProfileControllerImpl implements ProfileController {

    private final OrderService orderService;
    private final UserService userService;
    private final TableSessionService tableSessionService;

    @Override
    public ResponseEntity<UserResponseDto> getAuthenticatedUser() {

        return ResponseEntity.ok(userService.getAuthenticatedUser());
    }

    @Override
    public ResponseEntity<Page<OrderResponseDto>> getMyOrders(OrderStatus orderStatus, Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrdersByAuthenticatedClientAndStatus(orderStatus, pageable));
    }

    @Override
    public ResponseEntity<AuthResponse> createTableSession(
            TableSessionCreateDto tableSessionCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).
                body(tableSessionService.create(tableSessionCreateDto));
    }

    @Override
    public ResponseEntity<Page<OrderResponseDto>> getMyCurrentTableSessionOrders(OrderStatus orderStatus, Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrdersByAuthenticatedClientAndCurrentTableSessionAndStatus(orderStatus, pageable));
    }

    @Override
    public ResponseEntity<List<TableSessionResponseDto>> getTableSessionsByAuthUserHostClient() {
        return ResponseEntity.ok(tableSessionService.getByAuthUserHostClient());
    }

    @Override
    public ResponseEntity<List<TableSessionResponseDto>> getPastTableSessionsByAuthUserParticipant() {
        return ResponseEntity.ok(tableSessionService.getPastByAuthUserParticipant());
    }

}
