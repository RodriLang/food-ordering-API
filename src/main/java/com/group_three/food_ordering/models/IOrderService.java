package com.group_three.food_ordering.models;


import com.group_three.food_ordering.Order;
import com.group_three.food_ordering.dtos.OrderCreateDto;
import com.group_three.food_ordering.dtos.OrderResponseDto;
import com.group_three.food_ordering.dtos.OrderUpdateDto;

import java.util.List;
import java.util.UUID;

public interface IOrderService {

    OrderResponseDto create(OrderCreateDto orderCreateDto);
    List<OrderResponseDto> getAll();
    OrderResponseDto getById(UUID id);
    OrderResponseDto update(OrderUpdateDto orderUpdateDto);
    void delete(UUID id);
}
