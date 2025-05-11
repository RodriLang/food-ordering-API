package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.dtos.OrderRequestDto;
import com.group_three.food_ordering.dtos.OrderResponseDto;
import com.group_three.food_ordering.dtos.OrderUpdateDto;
import com.group_three.food_ordering.services.interfaces.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> create(
            @RequestBody OrderRequestDto order) {
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

    @PutMapping
    public ResponseEntity<OrderResponseDto> update(
            @RequestBody OrderUpdateDto order) {
        return ResponseEntity.ok(orderService.update(order));
    }

    @PatchMapping
    public ResponseEntity<OrderResponseDto> patch(
            @RequestBody OrderUpdateDto order){
        return ResponseEntity.ok(orderService.update(order));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable UUID id) {

        orderService.delete(id);

        return ResponseEntity.noContent().build();
    }

}
