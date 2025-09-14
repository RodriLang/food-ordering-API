package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dto.request.ProductCreateDto;
import com.group_three.food_ordering.dto.response.ProductResponseDto;
import com.group_three.food_ordering.models.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponseDto toDTO(Product product);

    Product toEntity(ProductCreateDto productCreateDto);
}
