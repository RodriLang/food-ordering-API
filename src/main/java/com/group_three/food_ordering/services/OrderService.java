package com.group_three.food_ordering.services;


import com.group_three.food_ordering.dto.request.OrderRequestDto;
import com.group_three.food_ordering.dto.response.OrderResponseDto;
import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.models.Order;
import com.group_three.food_ordering.models.OrderDetail;
import com.group_three.food_ordering.models.Participant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface OrderService {

    Order getEntityById(UUID id);

    OrderResponseDto create(OrderRequestDto orderRequestDto);

    OrderResponseDto getByIdAndTenantContext(UUID id);

    OrderResponseDto updateSpecialRequirements(UUID id, String specialRequirements);

    OrderResponseDto updateStatus(UUID orderId, OrderStatus orderStatus);

    OrderResponseDto getOrderByDateAndOrderNumber(LocalDate date, Integer orderNumber);

    Page<OrderResponseDto> getOrdersByFilters(LocalDate from, LocalDate to, OrderStatus orderStatus, Pageable pageable);

    List<Order> getOrderEntitiesByFilters(LocalDate from, LocalDate to, OrderStatus orderStatus);

    Page<OrderResponseDto> getOrdersForToday(OrderStatus orderStatus, Pageable pageable);

    List<Order> getOrderEntitiesForToday(OrderStatus orderStatus);

    Page<OrderResponseDto> getOrdersByTableSessionAndStatus(UUID tableSessionId, OrderStatus status, Pageable pageable);

    List<Order> getOrderEntitiesByTableSessionAndStatus(UUID tableSessionId, OrderStatus status);

    Page<OrderResponseDto> getOrdersByAuthenticatedClient(OrderStatus status, Pageable pageable);

    Page<OrderResponseDto> getOrdersByAuthenticatedClientAndStatus(OrderStatus orderStatus, Pageable pageable);

    Page<OrderResponseDto> getOrdersByAuthenticatedClientAndCurrentTableSessionAndStatus(OrderStatus status, Pageable pageable);

    Page<OrderResponseDto> getOrdersByClientAndTableSessionAndStatus(UUID clientId, UUID tableSessionId, OrderStatus status, Pageable pageable);

    Page<OrderResponseDto> getAllOrdersByCurrentTableSessionAndStatus(OrderStatus status, Pageable pageable);

    Page<OrderResponseDto> getOrdersByCurrentParticipant(Pageable pageable);

    List<Order> getOrderEntitiesByCurrentParticipant();

    Integer reassignOrdersToParticipant(Participant guest, Participant existing);

    void delete(UUID id);

    void addOrderDetailToOrder(UUID orderId, OrderDetail orderDetail);

    void removeOrderDetailFromOrder(UUID orderId, OrderDetail orderDetail);
}
