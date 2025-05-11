package com.group_three.food_ordering.mappers;


import com.group_three.food_ordering.models.Order;
import com.group_three.food_ordering.dtos.create.OrderCreateDto;
import com.group_three.food_ordering.dtos.response.OrderResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponseDto toDTO(OrderCreateDto orderCreateDto);

    OrderResponseDto toDTO(Order order);

    Order toEntity(OrderResponseDto orderDTO);

    Order toEntity(OrderCreateDto orderDTO);
}
