package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dto.request.CategoryRequestDto;
import com.group_three.food_ordering.dto.response.CategoryResponseDto;
import com.group_three.food_ordering.models.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping (target = "parentCategory", ignore = true)
    Category toEntity(CategoryRequestDto categoryRequestDto);

    @Mapping(target = "childrenCategories", qualifiedByName = "mapChildren")
    CategoryResponseDto toDto(Category category);

    @Named("mapChildren")
    default List<CategoryResponseDto> mapChildren(List<Category> children)
    {
        if (children == null)
        {
            return Collections.emptyList();
        }

        return children.stream()
                .map(this::toDto)
                .toList();
    }
}
