package com.group_three.food_ordering.services.interfaces;

import com.group_three.food_ordering.dtos.create.OrderDetailRequestDto;
import com.group_three.food_ordering.dtos.response.OrderDetailResponseDto;

import java.util.List;
import java.util.UUID;

public interface IOrderDetailService {

    OrderDetailResponseDto create(UUID orderId, OrderDetailRequestDto orderDetailRequestDto);

    List<OrderDetailResponseDto> getAll();

    List<OrderDetailResponseDto> getOrderDetailsByOrderId(UUID orderId);

    OrderDetailResponseDto getOrderDetailById(Long orderDetailId);

    void softDelete(UUID orderId, Long orderDetailId);

    OrderDetailResponseDto updateQuantity(Long orderDetailId, Integer newQuantity);

    OrderDetailResponseDto updateSpecialInstructions(Long orderDetailId, String instructions);

}
