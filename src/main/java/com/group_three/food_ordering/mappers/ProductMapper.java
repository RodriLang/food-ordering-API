package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dto.request.ProductRequestDto;
import com.group_three.food_ordering.dto.response.ItemMenuResponseDto;
import com.group_three.food_ordering.dto.response.ProductResponseDto;
import com.group_three.food_ordering.models.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, TagMapper.class})
public interface ProductMapper {

    ProductResponseDto toDto(Product product);

    @Mapping(target = "category", source = "category.name")
    ItemMenuResponseDto toItemMenuDto(Product product);

    Product toEntity(ProductRequestDto productRequestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Product productEntity, ProductRequestDto dto);
}
