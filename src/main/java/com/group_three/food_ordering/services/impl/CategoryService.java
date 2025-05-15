package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dtos.create.CategoryCreateDto;
import com.group_three.food_ordering.dtos.response.CategoryResponseDto;
import com.group_three.food_ordering.mappers.CategoryMapper;
import com.group_three.food_ordering.models.Category;
import com.group_three.food_ordering.repositories.ICategoryRepository;
import com.group_three.food_ordering.services.interfaces.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor

public class CategoryService implements ICategoryService {

    private final ICategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;


    @Override
    public CategoryResponseDto create(CategoryCreateDto categoryCreateDto) {
        Category category = categoryMapper.toEntity(categoryCreateDto);

        if (categoryCreateDto.getParentCategoryId() != null) {
            Category parent = categoryRepository.findById(categoryCreateDto.getParentCategoryId())
                    .orElseThrow(() -> new NoSuchElementException("Parent category not found"));
            category.setParentCategory(parent);
        }

        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryResponseDto update(Long id, CategoryCreateDto categoryCreateDto) {
        return categoryMapper.toDto(categoryRepository.findById(id).orElseThrow());
    }

    @Override
    public CategoryResponseDto getById(Long id) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<CategoryResponseDto> getAll() {
        return List.of();
    }

    @Override
    public List<CategoryResponseDto> getRootCategories() {
        return List.of();
    }

    @Override
    public List<CategoryResponseDto> getCategoriesByParentCategoryId(Long id) {
        return List.of();
    }
}
