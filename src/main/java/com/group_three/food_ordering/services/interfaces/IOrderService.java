package com.group_three.food_ordering.services.interfaces;


import com.group_three.food_ordering.dtos.create.OrderRequestDto;
import com.group_three.food_ordering.dtos.response.OrderDetailResponseDto;
import com.group_three.food_ordering.dtos.response.OrderResponseDto;
import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.models.Order;
import com.group_three.food_ordering.models.OrderDetail;


import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IOrderService {

    Order getEntityById(UUID id);

    //----------- C R U D ----------//
    OrderResponseDto create(OrderRequestDto orderRequestDto);
    List<OrderResponseDto> getAll();
    OrderResponseDto getById(UUID id);
    void delete(UUID id);

    //----------- U P D A T E ----------//
    OrderResponseDto updateSpecialRequirements(UUID id, String specialRequirements);
    OrderResponseDto updateStatus(UUID orderId, OrderStatus orderStatus);
    void addOrderDetailToOrder(UUID orderId, OrderDetail orderDetail);
    void removeOrderDetailFromOrder(UUID orderId, OrderDetail orderDetail);

    OrderResponseDto getOrderByDateAndOrderNumber(LocalDate date, Integer orderNumber);

    List<OrderResponseDto> getOrdersByFilters(LocalDate from, LocalDate to, OrderStatus orderStatus);

    List<OrderResponseDto> getOrdersForToday(OrderStatus orderStatus);

    List<OrderResponseDto> getOrdersByTableSession(UUID tableSessionId);

    List<OrderDetailResponseDto>getOrderDetailsByOrderId(UUID orderId);

}
