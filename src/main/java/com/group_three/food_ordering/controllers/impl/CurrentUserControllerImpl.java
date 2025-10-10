package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.CurrentUserController;
import com.group_three.food_ordering.dto.request.UserRequestDto;
import com.group_three.food_ordering.dto.response.OrderResponseDto;
import com.group_three.food_ordering.dto.response.PageResponse;
import com.group_three.food_ordering.dto.response.TableSessionResponseDto;
import com.group_three.food_ordering.dto.response.UserDetailResponseDto;
import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.services.OrderService;
import com.group_three.food_ordering.services.TableSessionService;
import com.group_three.food_ordering.services.UserService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<UserDetailResponseDto> getAuthenticatedUser() {

        return ResponseEntity.ok(userService.getAuthenticatedUser());
    }

    @Override
    public ResponseEntity<UserDetailResponseDto> updateUser(UserRequestDto dto) {
        return ResponseEntity.ok(userService.updateAuthUser(dto));
    }

    @Override
    public ResponseEntity<UserDetailResponseDto> deleteUser() {
        userService.deleteAuthUser();
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<PageResponse<OrderResponseDto>> getMyOrders(OrderStatus orderStatus, Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(orderService.getOrdersByAuthenticatedClientAndStatus(orderStatus, pageable)));
    }

    @Override
    public ResponseEntity<PageResponse<OrderResponseDto>> getMyCurrentTableSessionOrders(OrderStatus orderStatus, Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(orderService.getOrdersByAuthenticatedClientAndCurrentTableSessionAndStatus(orderStatus, pageable)));
    }

    @Override
    public ResponseEntity<PageResponse<TableSessionResponseDto>> getTableSessionsByAuthUserHostClient(Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(tableSessionService.getByAuthUserHostClient(pageable)));
    }

    @Override
    public ResponseEntity<PageResponse<TableSessionResponseDto>> getPastTableSessionsByAuthUserParticipant(Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(tableSessionService.getPastByAuthUserParticipant(pageable)));
    }

}
