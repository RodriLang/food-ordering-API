package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dtos.create.ProductCreateDto;
import com.group_three.food_ordering.dtos.response.ProductResponseDto;
import com.group_three.food_ordering.models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponseDto toDTO(Product product);

    Product toEntity(ProductCreateDto productCreateDto);
}
