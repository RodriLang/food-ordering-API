package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dto.request.FeaturedProductRequestDto;
import com.group_three.food_ordering.dto.response.FeaturedProductResponseDto;
import com.group_three.food_ordering.models.FeaturedProduct;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, TagMapper.class})
public interface FeaturedProductMapper {

    FeaturedProductResponseDto toDto(FeaturedProduct featuredProduct);

    FeaturedProduct toEntity(FeaturedProductRequestDto featuredProductRequestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget FeaturedProduct productEntity, FeaturedProductRequestDto dto);
}
