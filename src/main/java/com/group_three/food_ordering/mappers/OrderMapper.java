package com.group_three.food_ordering.mappers;


import com.group_three.food_ordering.Order;
import com.group_three.food_ordering.dtos.OrderCreateDto;
import com.group_three.food_ordering.dtos.OrderResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderResponseDto toDTO(OrderCreateDto orderCreateDto);

    OrderResponseDto toDTO(Order order);

    Order toEntity(OrderResponseDto orderDTO);

    Order toEntity(OrderCreateDto orderDTO);
}
