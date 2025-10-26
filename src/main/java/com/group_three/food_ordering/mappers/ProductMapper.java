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

    @Mapping(target = "tags", ignore = true)
    Product toEntity(ProductRequestDto productRequestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "stock", ignore = true)
    @Mapping(target = "available", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicId", ignore = true)
    @Mapping(target = "foodVenue", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    void updateEntity(@MappingTarget Product productEntity, ProductRequestDto dto);

}
