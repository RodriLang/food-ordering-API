package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dto.request.CategoryCreateDto;
import com.group_three.food_ordering.dto.response.CategoryResponseDto;
import com.group_three.food_ordering.models.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    //On CategoryResponseDto the Attribute ChildrenCategories 'll be use mapChildren method
    @Mapping(target = "childrenCategories", qualifiedByName = "mapChildren")
    CategoryResponseDto toDto(Category category);

    //recursive method for mapping the Attribute ChildrenCategories in CategoryResponseDto
    @Named("mapChildren")
    default List<CategoryResponseDto> mapChildren(List<Category> children)
    {
        if (children == null)
        {
            return Collections.emptyList();
        }

        return children.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // for designate manually
    @Mapping (target = "parentCategory", ignore = true)
    Category toEntity(CategoryCreateDto categoryCreateDto);
}
