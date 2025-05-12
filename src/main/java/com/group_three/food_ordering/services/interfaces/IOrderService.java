package com.group_three.food_ordering.services.interfaces;


<<<<<<< HEAD
import com.group_three.food_ordering.dtos.OrderRequestDto;
import com.group_three.food_ordering.dtos.OrderResponseDto;
import com.group_three.food_ordering.dtos.OrderUpdateDto;
import com.group_three.food_ordering.enums.OrderStatus;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
=======
import com.group_three.food_ordering.dtos.create.OrderCreateDto;
import com.group_three.food_ordering.dtos.response.OrderResponseDto;
import com.group_three.food_ordering.dtos.update.OrderUpdateDto;
>>>>>>> 51b6d069bf85c3e1a3fade8bf7a763a32e77820e

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
