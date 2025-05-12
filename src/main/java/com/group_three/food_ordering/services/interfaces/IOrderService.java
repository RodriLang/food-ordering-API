package com.group_three.food_ordering.services.interfaces;


import com.group_three.food_ordering.dtos.create.OrderRequestDto;
import com.group_three.food_ordering.dtos.response.OrderResponseDto;
import com.group_three.food_ordering.dtos.update.OrderUpdateDto;
import com.group_three.food_ordering.enums.OrderStatus;


import java.util.List;
import java.util.UUID;

public interface IOrderService {

    OrderResponseDto create(OrderRequestDto orderRequestDto);
    List<OrderResponseDto> getAll();
    OrderResponseDto getById(UUID id);
    OrderResponseDto update(UUID id, OrderUpdateDto orderUpdateDto);
    void delete(UUID id);
    OrderResponseDto updateStatus(UUID orderId, OrderStatus orderStatus);
}
