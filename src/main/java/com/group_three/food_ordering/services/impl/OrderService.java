package com.group_three.food_ordering.services.impl;


import com.group_three.food_ordering.models.Order;
import com.group_three.food_ordering.dtos.create.OrderCreateDto;
import com.group_three.food_ordering.dtos.response.OrderResponseDto;
import com.group_three.food_ordering.dtos.update.OrderUpdateDto;
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
    public OrderResponseDto create(OrderCreateDto orderCreateDto) {
        Order order = orderMapper.toEntity(orderCreateDto);

        return orderMapper.toDTO(orderRepository.save(order));
    }

    @Override
    public List<OrderResponseDto> getAll() {
        return  orderRepository.findAll().stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    @Override
    public OrderResponseDto getById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(OrderNotFoundException::new);
        return orderMapper.toDTO(order);
    }

    @Override
    public OrderResponseDto update(OrderUpdateDto orderUpdateDto) {
        Order order = new Order();
        orderRepository.save(order);
        return new OrderResponseDto();
    }

    @Override
    public void delete(UUID id) {
        orderRepository.deleteById(id);
    }
}
