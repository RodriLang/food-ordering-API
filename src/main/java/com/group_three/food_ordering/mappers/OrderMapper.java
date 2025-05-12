package com.group_three.food_ordering.mappers;


import com.group_three.food_ordering.models.Order;
<<<<<<< HEAD
import com.group_three.food_ordering.dtos.OrderRequestDto;
import com.group_three.food_ordering.dtos.OrderResponseDto;
=======
import com.group_three.food_ordering.dtos.create.OrderCreateDto;
import com.group_three.food_ordering.dtos.response.OrderResponseDto;
>>>>>>> 51b6d069bf85c3e1a3fade8bf7a763a32e77820e
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {

<<<<<<< HEAD
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderResponseDto toDTO(OrderRequestDto orderRequestDto);
=======
    OrderResponseDto toDTO(OrderCreateDto orderCreateDto);
>>>>>>> 51b6d069bf85c3e1a3fade8bf7a763a32e77820e

    OrderResponseDto toDTO(Order order);

    Order toEntity(OrderResponseDto orderDTO);

    Order toEntity(OrderRequestDto orderDTO);
}
