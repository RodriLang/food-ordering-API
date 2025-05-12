package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dtos.create.ProductCreateDto;
import com.group_three.food_ordering.dtos.response.ProductResponseDto;
import com.group_three.food_ordering.models.Product;
import org.mapstruct.Mapper;


@Mapper (componentModel = "spring")
public interface ProductMapper {
    Product toEntity(ProductCreateDto productDto);
    ProductResponseDto toDTO(Product product);
}
