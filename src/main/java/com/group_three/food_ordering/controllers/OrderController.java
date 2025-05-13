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


    // ========== ORDER ==========

    @PostMapping
    public ResponseEntity<OrderResponseDto> create(
            @RequestBody @Valid OrderRequestDto order) {
        return ResponseEntity.ok(orderService.create(order));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getOrders() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @PatchMapping("/{id}/requirements")
    public ResponseEntity<OrderResponseDto> updateRequirements(
            @PathVariable UUID id,
            @RequestParam @Size(max = 255) String requirements) {
        return ResponseEntity.ok(orderService.updateSpecialRequirements(id, requirements));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDto> updateStatus(
            @PathVariable UUID id,
            @RequestParam OrderStatus status){
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable UUID id) {

        orderService.delete(id);

        return ResponseEntity.noContent().build();
    }


    // ========== ORDER ==========


    @PostMapping("/{orderId}/order-details")
    public ResponseEntity<OrderResponseDto> addOrderDetail(
            @PathVariable UUID orderId,
            @RequestBody @Valid OrderDetailRequestDto orderDetailRequestDto){
        return ResponseEntity.ok(orderService.addOrderDetail(orderId, orderDetailRequestDto));
    }

    @GetMapping("/{orderId}/order-details")
    public ResponseEntity<List<OrderDetailResponseDto>> getOrderDetails(
            @PathVariable UUID orderId){
        return ResponseEntity.ok(orderService.getOrderDetailsByOrderId(orderId));
    }

    @DeleteMapping("/{orderId}/order-details/{orderDetailId}")
    public ResponseEntity<OrderResponseDto> removeOrderDetail(
            @PathVariable UUID orderId,
            @PathVariable Long orderDetailId){
        return ResponseEntity.ok(orderService.removeOrderDetail(orderId, orderDetailId));
    }
}
