package com.group_three.food_ordering.mappers;


import com.group_three.food_ordering.models.Order;
import com.group_three.food_ordering.dtos.OrderRequestDto;
import com.group_three.food_ordering.dtos.OrderResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderResponseDto toDTO(OrderRequestDto orderRequestDto);

    OrderResponseDto toDTO(Order order);

    Order toEntity(OrderResponseDto orderDTO);

    Order toEntity(OrderRequestDto orderDTO);
}
