package com.group_three.food_ordering.services.impl;


import com.group_three.food_ordering.dtos.create.OrderDetailRequestDto;
import com.group_three.food_ordering.dtos.create.OrderRequestDto;
import com.group_three.food_ordering.dtos.response.OrderDetailResponseDto;
import com.group_three.food_ordering.dtos.response.OrderResponseDto;
import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.enums.PaymentStatus;
import com.group_three.food_ordering.exceptions.MenuItemNotFoundException;
import com.group_three.food_ordering.exceptions.OrderDetailNotFoundException;
import com.group_three.food_ordering.exceptions.OrderInProgressException;
import com.group_three.food_ordering.mappers.OrderDetailMapper;
import com.group_three.food_ordering.models.MenuItem;
import com.group_three.food_ordering.models.Order;
import com.group_three.food_ordering.exceptions.OrderNotFoundException;
import com.group_three.food_ordering.mappers.OrderMapper;
import com.group_three.food_ordering.models.OrderDetail;
import com.group_three.food_ordering.repositories.IMenuItemRepository;
import com.group_three.food_ordering.repositories.IOrderDetailRepository;
import com.group_three.food_ordering.repositories.IOrderRepository;
import com.group_three.food_ordering.services.interfaces.IOrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final IOrderRepository orderRepository;
    private final IOrderDetailRepository orderDetailRepository;
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final IMenuItemRepository menuItemRepository;

    @Override
    public OrderResponseDto create(OrderRequestDto orderRequestDto) {
        Order order = orderMapper.toEntity(orderRequestDto);

        return orderMapper.toDTO(orderRepository.save(order));
    }


    @Override
    public List<OrderResponseDto> getAll() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    @Override
    public OrderResponseDto getById(UUID id) {

        return orderMapper.toDTO(this.getOrderById(id));
    }

    @Override
    public OrderResponseDto updateSpecialRequirements(UUID orderId, String specialRequirements) {
        Order existingOrder = this.getOrderById(orderId);

        existingOrder.setSpecialRequirements(specialRequirements);

        orderRepository.save(existingOrder);

        return new OrderResponseDto();    }

    @Transactional
    @Override
    public OrderResponseDto addOrderDetail(UUID orderId, OrderDetailRequestDto orderDetailRequestDto) {
        Order existingOrder = this.getOrderById(orderId);

        MenuItem menuItem = menuItemRepository.findById(orderDetailRequestDto.getMenuItemId())
                .orElseThrow(MenuItemNotFoundException::new);

        OrderDetail orderDetail = orderDetailMapper.toEntity(orderDetailRequestDto);
        orderDetail.setMenuItem(menuItem);
        orderDetail.setOrder(existingOrder);

        existingOrder.getOrderDetails().add(orderDetail);

        return orderMapper.toDTO(orderRepository.save(existingOrder));
    }

    @Transactional
    @Override
    public OrderResponseDto removeOrderDetail(UUID orderId, Long orderDetailId) {
        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(OrderDetailNotFoundException::new);

        Order order = this.getOrderById(orderId);

        if (!orderDetail.getOrder().equals(order)) {
            throw new IllegalArgumentException("OrderDetail does not belong to Order");
        }

        orderDetailRepository.delete(orderDetail);

        updateTotalPrice(order);
        orderRepository.save(order);

        return orderMapper.toDTO(order);
    }

    @Override
    public void delete(UUID id) {
        orderRepository.deleteById(id);
    }

    @Override
    public OrderResponseDto updateStatus(UUID id, OrderStatus orderStatus) {
        Order existingOrder = this.getOrderById(id);

        existingOrder.setStatus(orderStatus);
        orderRepository.save(existingOrder);

        return orderMapper.toDTO(existingOrder);
    }

    @Override
    public List<OrderDetailResponseDto> getOrderDetailsByOrderId(UUID orderId) {
        return orderDetailRepository.findAllByOrder_Id(orderId).stream()
                .map(orderDetailMapper::toDTO)
                .toList();
    }

    private Order getOrderById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(OrderNotFoundException::new);
    }

    private void updateTotalPrice(Order order) {
        BigDecimal totalPrice = order.getOrderDetails().stream()
                .map(orderDetail -> orderDetail.getPrice().multiply(new BigDecimal(orderDetail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalPrice(totalPrice);
    }

    private void validateUpdate(Order order) {
        // La orden solo puede modificarse si está en estado PENDING, o si está en estado APPROVED
        // pero su pago aún no ha sido realizado (es decir, el pago está en estado PENDING).
        if (order.getStatus() == OrderStatus.PENDING ||
                (order.getStatus() == OrderStatus.APPROVED &&
                        (order.getPayment() != null && order.getPayment().getPaymentStatus() == PaymentStatus.PENDING))) {
            // La orden puede ser modificada, no hacemos nada
            return;
        }

        // Si no cumple con las condiciones anteriores, lanzamos la excepción
        throw new OrderInProgressException(order.getId());
    }



}
