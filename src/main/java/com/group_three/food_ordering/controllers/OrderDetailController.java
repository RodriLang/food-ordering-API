package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dtos.create.OrderDetailRequestDto;
import com.group_three.food_ordering.dtos.response.OrderDetailResponseDto;
import com.group_three.food_ordering.services.interfaces.IOrderDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.ORDER_BASE)
@RequiredArgsConstructor
public class OrderDetailController {

    private final IOrderDetailService orderDetailService;


    // ========== ORDER DETAIL ENDPOINTS ==========

    @PostMapping(ApiPaths.ORDER_DETAIL_URI)
    public ResponseEntity<OrderDetailResponseDto> addOrderDetailToOrder(
            @PathVariable UUID orderId,
            @RequestBody @Valid OrderDetailRequestDto orderDetailRequestDto) {
        return ResponseEntity.ok(orderDetailService.create(orderId, orderDetailRequestDto));
    }

    @GetMapping(ApiPaths.ORDER_DETAIL_URI)
    public ResponseEntity<List<OrderDetailResponseDto>> getOrderDetailsByOrderId(
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(orderDetailService.getOrderDetailsByOrderId(orderId));
    }

    @DeleteMapping("/order-details/{orderDetailId}")
    public ResponseEntity<Void> removeOrderDetailFromOrder(
            @PathVariable Long orderDetailId) {
        orderDetailService.softDelete(orderDetailId);
        return ResponseEntity.noContent().build();
    }
}
