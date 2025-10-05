package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.CategoryRequestDto;
import com.group_three.food_ordering.dto.response.CategoryResponseDto;
import com.group_three.food_ordering.models.Category;

import java.util.List;

public interface CategoryService {
    CategoryResponseDto create(CategoryRequestDto categoryRequestDto);
    CategoryResponseDto update(Long id, CategoryRequestDto categoryRequestDto);
    CategoryResponseDto getById(Long id);
    Category getEntityById(Long id);
    void delete(Long id);
    List<CategoryResponseDto> getAll();
    List<CategoryResponseDto> getCategoriesByParentCategoryId(Long id);

}
