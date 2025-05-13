package com.group_three.food_ordering.mappers;


import com.group_three.food_ordering.dtos.create.OrderDetailRequestDto;
import com.group_three.food_ordering.dtos.response.OrderDetailResponseDto;
import com.group_three.food_ordering.models.OrderDetail;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {

    OrderDetailResponseDto toDTO(OrderDetail orderDetail);

    OrderDetail toEntity(OrderDetailRequestDto orderDetailDTO);
}
