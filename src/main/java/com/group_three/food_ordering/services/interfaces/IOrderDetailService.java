package com.group_three.food_ordering.services.interfaces;


import com.group_three.food_ordering.dtos.create.OrderDetailRequestDto;
import com.group_three.food_ordering.dtos.create.OrderRequestDto;
import com.group_three.food_ordering.dtos.response.OrderDetailResponseDto;
import com.group_three.food_ordering.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface IOrderDetailService {

    OrderDetailResponseDto create(UUID orderId, OrderDetailRequestDto orderRequestDto);
    List<OrderDetailResponseDto> getAll();
    List<OrderDetailResponseDto>getOrderDetailsByOrderId(UUID orderId);
    OrderDetailResponseDto getOrderDetailById(Long orderDetailId);
    void softDelete(Long orderDetailId);

    OrderDetailResponseDto updateQuantity(Long orderDetailId, Integer q);
    OrderDetailResponseDto updateSpecialInstructions(Long id, String instructions);
}
