package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.OrderController;
import com.group_three.food_ordering.dto.request.OrderRequestDto;
import com.group_three.food_ordering.dto.response.OrderResponseDto;
import com.group_three.food_ordering.dto.response.PageResponse;
import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderControllerImpl implements OrderController {

    private final OrderService orderService;

    @Override
    @PreAuthorize("hasAnyRole('CLIENT','GUEST')")
    public ResponseEntity<OrderResponseDto> createOrder(OrderRequestDto order) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(order));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER','ROOT')")
    public ResponseEntity<PageResponse<OrderResponseDto>> getOrders(LocalDate from, LocalDate to, OrderStatus status, Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(orderService.getOrdersByFilters(from, to, status, pageable)));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER','ROOT')")
    public ResponseEntity<PageResponse<OrderResponseDto>> getDailyOrders(OrderStatus status, Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(orderService.getOrdersForToday(status, pageable)));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER','ROOT')")
    public ResponseEntity<OrderResponseDto> getOrderById(UUID id) {
        return ResponseEntity.ok(orderService.getByIdAndTenantContext(id));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER','ROOT')")
    public ResponseEntity<OrderResponseDto> getOrderByDateAndOrderNumber(LocalDate date, Integer orderNumber) {
        return ResponseEntity.ok(orderService.getOrderByDateAndOrderNumber(date, orderNumber));
    }

    @Override
    @PreAuthorize("hasAnyRole('CLIENT', 'GUEST')")
    public ResponseEntity<OrderResponseDto> updateOrderRequirements(UUID id, String requirements) {
        return ResponseEntity.ok(orderService.updateSpecialRequirements(id, requirements));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CLIENT', 'GUEST', 'MANAGER','ROOT')")
    public ResponseEntity<OrderResponseDto> cancelOrder(UUID id) {
        return ResponseEntity.ok(orderService.updateStatus(id, OrderStatus.CANCELLED));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER','ROOT')")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(UUID id, OrderStatus status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }
}