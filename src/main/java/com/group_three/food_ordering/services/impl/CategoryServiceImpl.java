package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.create.CategoryCreateDto;
import com.group_three.food_ordering.dto.response.CategoryResponseDto;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.CategoryMapper;
import com.group_three.food_ordering.models.Category;
import com.group_three.food_ordering.repositories.CategoryRepository;
import com.group_three.food_ordering.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;


    @Override
    public CategoryResponseDto create(CategoryCreateDto categoryCreateDto) {
        Category category = categoryMapper.toEntity(categoryCreateDto);
        if (categoryCreateDto.getParentCategoryId() != null) {
            Category parent = getEntityById(categoryCreateDto.getParentCategoryId());
            category.setParentCategory(parent);
        }
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryResponseDto update(Long id, CategoryCreateDto categoryCreateDto) {
        Category category = getEntityById(categoryCreateDto.getParentCategoryId());
        category.setName(categoryCreateDto.getName());
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryResponseDto getById(Long id) {
        Category category = getEntityById(id);
        return categoryMapper.toDto(category);
    }

    @Override
    public Category getEntityById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Category"));

    }

    @Override
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryResponseDto> getAll() {
        List<Category> roots = categoryRepository.findByParentCategoryIsNullAndDeletedFalse();

        return roots.stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public List<CategoryResponseDto> getCategoriesByParentCategoryId(Long id) {
       List<Category> children = categoryRepository.findByParentCategoryIdAndDeletedFalse(id);
       return children.stream()
               .map(categoryMapper::toDto)
               .toList();
    }
}
