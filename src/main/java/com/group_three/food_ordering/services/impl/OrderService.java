package com.group_three.food_ordering.services.impl;


import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dtos.create.OrderRequestDto;
import com.group_three.food_ordering.dtos.response.OrderDetailResponseDto;
import com.group_three.food_ordering.dtos.response.OrderResponseDto;
import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.enums.PaymentStatus;
import com.group_three.food_ordering.exceptions.OrderInProgressException;
import com.group_three.food_ordering.mappers.OrderDetailMapper;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.models.Order;
import com.group_three.food_ordering.exceptions.OrderNotFoundException;
import com.group_three.food_ordering.mappers.OrderMapper;
import com.group_three.food_ordering.models.OrderDetail;
import com.group_three.food_ordering.repositories.IOrderDetailRepository;
import com.group_three.food_ordering.repositories.IOrderRepository;
import com.group_three.food_ordering.services.interfaces.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final IOrderRepository orderRepository;
    private final IOrderDetailRepository orderDetailRepository;
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final TenantContext tenantContext;

    @Override
    public OrderResponseDto create(OrderRequestDto orderRequestDto) {
        Order order = orderMapper.toEntity(orderRequestDto);
        order.setOrderNumber(this.generateOrderNumber());
        return orderMapper.toDTO(orderRepository.save(order));
    }

    @Override
    public List<OrderResponseDto> getAll() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    public List<OrderResponseDto> getOrders(LocalDate from, LocalDate to, OrderStatus status) {
        LocalDateTime fromDateTime = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime toDateTime = (to != null) ? to.atTime(LocalTime.MAX) : null;

        List<Order> orders;

        if (fromDateTime != null && toDateTime != null && status != null) {
            orders = orderRepository.findByFoodVenue_IdAndCreationDateBetweenAndStatus(
                    tenantContext.getCurrentFoodVenue().getId(), fromDateTime, toDateTime, status);
        } else if (fromDateTime != null && toDateTime != null) {
            orders = orderRepository.findByFoodVenue_IdAndCreationDateBetween(
                    tenantContext.getCurrentFoodVenue().getId(), fromDateTime, toDateTime);
        } else if (status != null) {
            orders = orderRepository.findByFoodVenue_IdAndStatus(tenantContext.getCurrentFoodVenue().getId(), status);
        } else {
            orders = orderRepository.findAll();
        }

        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }


    @Override
    public OrderResponseDto getById(UUID id) {

        return orderMapper.toDTO(this.getEntityById(id));
    }

    @Override
    public OrderResponseDto updateSpecialRequirements(UUID orderId, String specialRequirements) {
        Order existingOrder = this.getEntityById(orderId);

        existingOrder.setSpecialRequirements(specialRequirements);

        orderRepository.save(existingOrder);

        return new OrderResponseDto();
    }


    @Override
    public void delete(UUID id) {
        orderRepository.deleteById(id);
    }

    @Override
    public OrderResponseDto updateStatus(UUID id, OrderStatus orderStatus) {
        Order existingOrder = this.getEntityById(id);

        existingOrder.setStatus(orderStatus);
        orderRepository.save(existingOrder);

        return orderMapper.toDTO(existingOrder);
    }

    @Override
    public List<OrderDetailResponseDto> getOrderDetailsByOrderId(UUID orderId) {
        return orderDetailRepository.findAllByOrder_IdAndDeletedFalse(orderId)
                .stream()
                .map(orderDetailMapper::toDTO)
                .toList();
    }

    @Override
    public OrderResponseDto getDailyOrderByOrderNumber(
            UUID venueId, Integer orderNumber) {

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        return this.getOrderByOrderNumberAndDateBetween(venueId, orderNumber, start, end);
    }

    @Override
    public OrderResponseDto getOrderByOrderNumberAndDateBetween(
            UUID venueId, Integer orderNumber, LocalDateTime start, LocalDateTime end) {

        return orderMapper.toDTO(orderRepository
                .findByFoodVenue_IdAndOrderNumberAndCreationDateBetween(
                        venueId, orderNumber, start, end)
                .orElseThrow(OrderNotFoundException::new));
    }


    @Override
    public List<OrderResponseDto> getDailyOrders() {

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        return orderRepository.findByFoodVenue_IdAndCreationDateBetween(tenantContext.getCurrentFoodVenue().getId(), start, end)
                .stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    @Override
    public List<OrderResponseDto> getDailyOrdersByDateBetween(UUID foodVenueId, LocalDateTime start, LocalDateTime end) {
        return List.of();
    }

    @Override
    public List<OrderResponseDto> getOrdersByTableSessionId(UUID tableSessionId) {
        return orderRepository.findOrderByTableSession_Id(tableSessionId).stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    @Override
    public void removeOrderDetailFromOrder(UUID orderId, OrderDetail orderDetail) {

        Order existingOrder = this.getEntityById(orderId);
        this.validateUpdate(existingOrder);
        existingOrder.getOrderDetails().remove(orderDetail);
        updateTotalPrice(existingOrder);

        this.orderRepository.save(existingOrder);
    }

    @Override
    public void addOrderDetailToOrder(UUID orderId, OrderDetail orderDetail) {

        Order existingOrder = this.getEntityById(orderId);
        this.validateUpdate(existingOrder);
        existingOrder.getOrderDetails().add(orderDetail);
        updateTotalPrice(existingOrder);

        this.orderRepository.save(existingOrder);
    }

    @Override
    public Order getEntityById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(OrderNotFoundException::new);
    }


    private void updateTotalPrice(Order order) {
        BigDecimal totalPrice = order.getOrderDetails().stream()
                .map(orderDetail
                        -> orderDetail.getPrice().multiply(
                        new BigDecimal(orderDetail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalPrice(totalPrice);
    }

    private void validateUpdate(Order order) {
        // La orden solo puede modificarse si está en estado PENDING, o si está en estado APPROVED
        // y su pago aún no ha sido realizado (es decir, el pago está en estado PENDING).
        if (order.getStatus() == OrderStatus.PENDING ||
                (order.getStatus() == OrderStatus.APPROVED &&
                        (order.getPayment() != null && order.getPayment().getStatus() == PaymentStatus.PENDING))) {
            // La orden puede ser modificada, no hacemos nada
            return;
        }

        // Si no cumple con las condiciones anteriores, lanzamos la excepción
        throw new OrderInProgressException(order.getId());
    }

    private Integer generateOrderNumber() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        int ordersCount = Math.toIntExact(orderRepository.countOrdersToday(UUID.randomUUID(), start, end));

        return ordersCount + 1;
    }

}
