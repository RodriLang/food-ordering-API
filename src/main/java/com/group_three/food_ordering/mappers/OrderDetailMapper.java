package com.group_three.food_ordering.mappers;


import com.group_three.food_ordering.dtos.create.OrderDetailRequestDto;
import com.group_three.food_ordering.dtos.response.OrderDetailResponseDto;
import com.group_three.food_ordering.models.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {

    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.category.name", target = "category")
    @Mapping(source = "product.price", target = "unitPrice")
    @Mapping(target = "subtotal",
            expression = "java(orderDetail.getPrice() != null ? orderDetail.getPrice().multiply(java.math.BigDecimal.valueOf(orderDetail.getQuantity())) : java.math.BigDecimal.ZERO)")

    OrderDetailResponseDto toDTO(OrderDetail orderDetail);

    OrderDetail toEntity(OrderDetailRequestDto orderDetailDTO);

}
