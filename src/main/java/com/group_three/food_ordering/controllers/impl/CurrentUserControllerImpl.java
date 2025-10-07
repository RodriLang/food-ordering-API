package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.CurrentUserController;
import com.group_three.food_ordering.dto.request.UserRequestDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasAnyRole('CLIENT')")
@RestController
@RequiredArgsConstructor
public class CurrentUserControllerImpl implements CurrentUserController {

    private final OrderService orderService;
    private final UserService userService;
    private final TableSessionService tableSessionService;

    @Override
    public ResponseEntity<UserResponseDto> getAuthenticatedUser() {

        return ResponseEntity.ok(userService.getAuthenticatedUser());
    }

    @Override
    public ResponseEntity<UserResponseDto> updateUser(UserRequestDto dto) {
        return ResponseEntity.ok(userService.updateAuthUser(dto));
    }

    @Override
    public ResponseEntity<UserResponseDto> deleteUser() {
        userService.deleteAuthUser();
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Page<OrderResponseDto>> getMyOrders(OrderStatus orderStatus, Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrdersByAuthenticatedClientAndStatus(orderStatus, pageable));
    }

    @Override
    public ResponseEntity<Page<OrderResponseDto>> getMyCurrentTableSessionOrders(OrderStatus orderStatus, Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrdersByAuthenticatedClientAndCurrentTableSessionAndStatus(orderStatus, pageable));
    }

    @Override
    public ResponseEntity<Page<TableSessionResponseDto>> getTableSessionsByAuthUserHostClient(Pageable pageable) {
        return ResponseEntity.ok(tableSessionService.getByAuthUserHostClient(pageable));
    }

    @Override
    public ResponseEntity<Page<TableSessionResponseDto>> getPastTableSessionsByAuthUserParticipant(Pageable pageable) {
        return ResponseEntity.ok(tableSessionService.getPastByAuthUserParticipant(pageable));
    }

}
