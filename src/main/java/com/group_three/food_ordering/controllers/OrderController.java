package com.group_three.food_ordering.controllers;

<<<<<<< HEAD
import com.group_three.food_ordering.dtos.OrderRequestDto;
import com.group_three.food_ordering.dtos.OrderResponseDto;
import com.group_three.food_ordering.dtos.OrderUpdateDto;
import com.group_three.food_ordering.enums.OrderStatus;
=======
import com.group_three.food_ordering.dtos.create.OrderCreateDto;
import com.group_three.food_ordering.dtos.response.OrderResponseDto;
import com.group_three.food_ordering.dtos.update.OrderUpdateDto;
>>>>>>> 51b6d069bf85c3e1a3fade8bf7a763a32e77820e
import com.group_three.food_ordering.services.interfaces.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;

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

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDto> update(
            @PathVariable UUID id,
            @RequestBody @Valid OrderUpdateDto order) {
        return ResponseEntity.ok(orderService.update(id,order));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDto> updateStatus(
            @PathVariable UUID id,
            @RequestParam OrderStatus status){
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }

    @PatchMapping("/{id}/")
    public ResponseEntity<OrderResponseDto> patch(
            @PathVariable UUID id,
            @RequestParam OrderStatus status){
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable UUID venueId,
            @PathVariable UUID id) {

        orderService.delete(id);

        return ResponseEntity.noContent().build();
    }

}
