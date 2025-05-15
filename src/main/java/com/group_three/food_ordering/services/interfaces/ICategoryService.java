package com.group_three.food_ordering.services.interfaces;

import com.group_three.food_ordering.dtos.create.CategoryCreateDto;
import com.group_three.food_ordering.dtos.response.CategoryResponseDto;

import java.util.List;

public interface ICategoryService {
    CategoryResponseDto create(CategoryCreateDto categoryCreateDto);
    CategoryResponseDto update(Long id, CategoryCreateDto categoryCreateDto);
    CategoryResponseDto getById(Long id);
    void delete(Long id);
    List<CategoryResponseDto> getAll();
    List<CategoryResponseDto> getRootCategories();
    List<CategoryResponseDto> getCategoriesByParentCategoryId(Long id);

}
