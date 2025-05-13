package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dtos.create.OrderDetailRequestDto;
import com.group_three.food_ordering.dtos.create.OrderRequestDto;
import com.group_three.food_ordering.dtos.response.OrderDetailResponseDto;
import com.group_three.food_ordering.dtos.response.OrderResponseDto;
import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.services.interfaces.IOrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.ORDER_BASE)
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;


    // ========== ORDER ENDPOINTS ==========

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestBody @Valid OrderRequestDto order) {
        return ResponseEntity.ok(orderService.create(order));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getOrders() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getById(orderId));
    }

    @PatchMapping("/{orderId}/requirements")
    public ResponseEntity<OrderResponseDto> updateOrderRequirements(
            @PathVariable UUID orderId,
            @RequestParam @Size(max = 255) String requirements) {
        return ResponseEntity.ok(orderService.updateSpecialRequirements(orderId, requirements));
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateStatus(orderId, status));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable UUID orderId) {

        orderService.delete(orderId);

        return ResponseEntity.noContent().build();
    }


    // ========== ORDER DETAIL ENDPOINTS ==========

    @PostMapping(ApiPaths.ORDER_DETAIL_URI)
    public ResponseEntity<OrderResponseDto> addOrderDetailToOrder(
            @PathVariable UUID orderId,
            @RequestBody @Valid OrderDetailRequestDto orderDetailRequestDto) {
        return ResponseEntity.ok(orderService.addOrderDetail(orderId, orderDetailRequestDto));
    }

    @GetMapping(ApiPaths.ORDER_DETAIL_URI)
    public ResponseEntity<List<OrderDetailResponseDto>> getOrderDetailsByOrderId(
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderDetailsByOrderId(orderId));
    }

    @DeleteMapping(ApiPaths.ORDER_DETAIL_URI + "/{orderDetailId}")
    public ResponseEntity<OrderResponseDto> removeOrderDetailFromOrder(
            @PathVariable UUID orderId,
            @PathVariable Long orderDetailId) {
        return ResponseEntity.ok(orderService.removeOrderDetail(orderId, orderDetailId));
    }
}
