package com.group_three.food_ordering.services.impl;


import com.group_three.food_ordering.dtos.create.OrderRequestDto;
import com.group_three.food_ordering.dtos.response.OrderResponseDto;
import com.group_three.food_ordering.dtos.update.OrderUpdateDto;
import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.models.Order;
import com.group_three.food_ordering.exceptions.OrderNotFoundException;
import com.group_three.food_ordering.mappers.OrderMapper;
import com.group_three.food_ordering.repositories.IOrderRepository;
import com.group_three.food_ordering.services.interfaces.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final IOrderRepository orderRepository;
    private final OrderMapper orderMapper;

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
        Order order = orderRepository.findByOrderIdAndVenueId(id, UUID.randomUUID())
                .orElseThrow(OrderNotFoundException::new);
        return orderMapper.toDTO(order);
    }

    @Override
    public OrderResponseDto update(UUID id, OrderUpdateDto orderUpdateDto) {
        Order order = new Order();
        orderRepository.save(order);
        return new OrderResponseDto();

    }


    @Override
    public void delete(UUID id) {
        orderRepository.deleteById(id);
    }

    @Override
    public OrderResponseDto updateStatus(UUID id, OrderStatus orderStatus) {
        Order existingOrder = orderRepository.findByOrderIdAndVenueId(id, UUID.randomUUID())
                .orElseThrow(OrderNotFoundException::new);

        existingOrder.setStatus(orderStatus);
        orderRepository.save(existingOrder);

        return orderMapper.toDTO(existingOrder);
    }
}
