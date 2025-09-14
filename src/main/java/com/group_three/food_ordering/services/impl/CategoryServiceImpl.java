package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.request.CategoryCreateDto;
import com.group_three.food_ordering.dto.response.CategoryResponseDto;
import com.group_three.food_ordering.mappers.CategoryMapper;
import com.group_three.food_ordering.models.Category;
import com.group_three.food_ordering.repositories.CategoryRepository;
import com.group_three.food_ordering.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor

public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
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
        Category category = categoryRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Category not found"));
        category.setName(categoryCreateDto.getName());
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryResponseDto getById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Category not found"));
        return categoryMapper.toDto(category);
    }

    @Override
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryResponseDto> getAll() {
        List<Category> roots = categoryRepository.findByParentCategoryIsNull();

        return roots.stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public List<CategoryResponseDto> getCategoriesByParentCategoryId(Long id) {
       List<Category> children = categoryRepository.findByParentCategoryId(id);
       return children.stream()
               .map(categoryMapper::toDto)
               .toList();
    }
}
