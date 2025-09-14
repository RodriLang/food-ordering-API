package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.OrderDetailRequestDto;
import com.group_three.food_ordering.dto.response.OrderDetailResponseDto;

import java.util.List;

public interface OrderDetailService {

    OrderDetailResponseDto create(OrderDetailRequestDto orderDetailRequestDto);

    List<OrderDetailResponseDto> getAll();

    OrderDetailResponseDto getOrderDetailById(Long orderDetailId);

    void softDelete(Long orderDetailId);

    void updateQuantity(Long orderDetailId, Integer newQuantity);

    OrderDetailResponseDto updateSpecialInstructions(Long orderDetailId, String instructions);

}
