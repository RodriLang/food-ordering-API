package com.group_three.food_ordering.services;


import com.group_three.food_ordering.dtos.OrderRequestDto;
import com.group_three.food_ordering.dtos.OrderResponseDto;
import com.group_three.food_ordering.dtos.OrderUpdateDto;

import java.util.List;
import java.util.UUID;

public interface IOrderService {

    OrderResponseDto create(OrderRequestDto orderRequestDto);
    List<OrderResponseDto> getAll();
    OrderResponseDto getById(UUID id);
    OrderResponseDto update(OrderUpdateDto orderUpdateDto);
    void delete(UUID id);
}
