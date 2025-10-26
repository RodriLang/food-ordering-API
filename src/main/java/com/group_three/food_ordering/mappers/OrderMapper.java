package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dto.request.OrderRequestDto;
import com.group_three.food_ordering.dto.response.OrderResponseDto;
import com.group_three.food_ordering.models.Order;
import com.group_three.food_ordering.utils.FormatUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderDetailMapper.class, FormatUtils.class})
public interface OrderMapper {

    @Mapping(source = "participant.nickname", target = "clientAlias")
    @Mapping(source = "orderNumber", target = "orderNumber", qualifiedByName = "formatOrderNumber")
    OrderResponseDto toDto(Order order);

    Order toEntity(OrderRequestDto orderDTO);

}
