package com.group_three.food_ordering.mappers;


import com.group_three.food_ordering.dto.request.OrderRequestDto;
import com.group_three.food_ordering.dto.response.OrderResponseDto;
import com.group_three.food_ordering.models.Order;
import com.group_three.food_ordering.utils.FormatUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {OrderDetailMapper.class})
public interface OrderMapper {

    @Mapping(source = "orderNumber",
            target = "formattedOrderNumber",
            qualifiedByName = "formatOrderNumber")
    @Mapping(source = "participant.nickname", target = "clientAlias")
    @Mapping(source = "orderDetails", target = "orderDetails")

    OrderResponseDto toDTO(Order order);

    Order toEntity(OrderRequestDto orderDTO);

    @Named("formatOrderNumber")
    static String formatOrderNumber(Integer orderNumber) {
        return FormatUtils.formatOrderNumber(orderNumber);
    }
}
