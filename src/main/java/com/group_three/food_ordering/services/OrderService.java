package com.group_three.food_ordering.services;


import com.group_three.food_ordering.dto.request.OrderRequestDto;
import com.group_three.food_ordering.dto.response.OrderResponseDto;
import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.models.Order;
import com.group_three.food_ordering.models.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.time.LocalDate;
import java.util.UUID;

public interface OrderService {

    Order getEntityByIdAndTenantContext(UUID id);

    OrderResponseDto create(OrderRequestDto orderRequestDto);

    OrderResponseDto getByIdAndTenantContext(UUID id);

    OrderResponseDto updateSpecialRequirements(UUID id, String specialRequirements);

    OrderResponseDto updateStatus(UUID orderId, OrderStatus orderStatus);

    OrderResponseDto getOrderByDateAndOrderNumber(LocalDate date, Integer orderNumber);

    Page<OrderResponseDto> getOrdersByFilters(LocalDate from, LocalDate to, OrderStatus orderStatus, Pageable pageable);

    Page<OrderResponseDto> getOrdersForToday(OrderStatus orderStatus, Pageable pageable);

    Page<OrderResponseDto> getOrdersByTableSessionAndStatus(UUID tableSessionId, OrderStatus orderStatus, Pageable pageable);

    Page<OrderResponseDto> getOrdersByAuthenticatedClient(OrderStatus status, Pageable pageable);

    Page<OrderResponseDto> getOrdersByAuthenticatedClientAndStatus(OrderStatus orderStatus, Pageable pageable);

    Page<OrderResponseDto> getOrdersByAuthenticatedClientAndCurrentTableSessionAndStatus(OrderStatus status, Pageable pageable);

    Page<OrderResponseDto> getOrdersByClientAndTableSessionAndStatus(UUID clientId, UUID tableSessionId, OrderStatus status, Pageable pageable);

    void delete(UUID id);

    void addOrderDetailToOrder(UUID orderId, OrderDetail orderDetail);

    void removeOrderDetailFromOrder(UUID orderId, OrderDetail orderDetail);

}
