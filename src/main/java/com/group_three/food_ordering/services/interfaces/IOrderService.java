package com.group_three.food_ordering.services.interfaces;


import com.group_three.food_ordering.dtos.create.OrderCreateDto;
import com.group_three.food_ordering.dtos.response.OrderResponseDto;
import com.group_three.food_ordering.dtos.update.OrderUpdateDto;

import java.util.List;
import java.util.UUID;

public interface IOrderService {

    OrderResponseDto create(OrderCreateDto orderCreateDto);
    List<OrderResponseDto> getAll();
    OrderResponseDto getById(UUID id);
    OrderResponseDto update(OrderUpdateDto orderUpdateDto);
    void delete(UUID id);
}
