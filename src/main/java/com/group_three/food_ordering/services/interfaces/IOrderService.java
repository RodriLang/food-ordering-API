package com.group_three.food_ordering.services.interfaces;


import com.group_three.food_ordering.dtos.create.OrderDetailRequestDto;
import com.group_three.food_ordering.dtos.create.OrderRequestDto;
import com.group_three.food_ordering.dtos.response.OrderDetailResponseDto;
import com.group_three.food_ordering.dtos.response.OrderResponseDto;
import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.models.Order;
import com.group_three.food_ordering.models.OrderDetail;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IOrderService {

    OrderResponseDto create(OrderRequestDto orderRequestDto);
    List<OrderResponseDto> getAll();
    OrderResponseDto getById(UUID id);
    Order getEntityById(UUID id);
    OrderResponseDto updateSpecialRequirements(UUID id, String specialRequirements);
    void delete(UUID id);
    OrderResponseDto updateStatus(UUID orderId, OrderStatus orderStatus);
    List<OrderDetailResponseDto>getOrderDetailsByOrderId(UUID orderId);
    void addOrderDetailToOrder(UUID orderId, OrderDetail orderDetail);
    void removeOrderDetailFromOrder(UUID orderId, OrderDetail orderDetail);
    OrderResponseDto getDailyOrderByOrderNumber(UUID venueId, Integer orderNumber);
    OrderResponseDto getOrderByOrderNumberAndDateBetween(UUID venueId, Integer orderNumber, LocalDateTime start, LocalDateTime end);
    List<OrderResponseDto> getOrdersByDateBetween(UUID foodVenueId, LocalDateTime start, LocalDateTime end);
    List<OrderResponseDto> getDailyOrders(UUID foodVenueId);
    List<OrderResponseDto> getDailyOrdersByDateBetween(UUID foodVenueId, LocalDateTime start, LocalDateTime end);
    List<OrderResponseDto> getOrdersByTableSessionId(UUID tableSessionId);

}
