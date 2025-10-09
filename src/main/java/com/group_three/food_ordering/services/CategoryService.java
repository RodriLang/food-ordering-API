package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.CategoryRequestDto;
import com.group_three.food_ordering.dto.response.CategoryResponseDto;
import com.group_three.food_ordering.models.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    CategoryResponseDto create(CategoryRequestDto categoryRequestDto);

    CategoryResponseDto update(UUID publicId, CategoryRequestDto categoryRequestDto);

    CategoryResponseDto getById(UUID publicId);

    Category getEntityById(UUID publicId);

    void delete(UUID publicId);

    List<CategoryResponseDto> getAll();

    List<CategoryResponseDto> getCategoriesByParentCategoryId(UUID publicId);

    List<Category> findParentCategories(UUID foodVenuePublicId);

}
