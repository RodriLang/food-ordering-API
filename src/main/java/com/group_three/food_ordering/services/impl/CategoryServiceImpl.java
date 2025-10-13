package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.request.CategoryRequestDto;
import com.group_three.food_ordering.dto.response.CategoryResponseDto;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.CategoryMapper;
import com.group_three.food_ordering.models.Category;
import com.group_three.food_ordering.repositories.CategoryRepository;
import com.group_three.food_ordering.services.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.group_three.food_ordering.utils.EntityName.CATEGORY;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final TenantContext tenantContext;

    @Override
    public CategoryResponseDto create(CategoryRequestDto categoryRequestDto) {
        Category category = categoryMapper.toEntity(categoryRequestDto);
        if (categoryRequestDto.getParentCategoryId() != null) {
            Category parent = getEntityById(categoryRequestDto.getParentCategoryId());
            category.setParentCategory(parent);
        }
        log.debug("[CategoryRepository] Calling save to create new category: {}", category.getName());
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryResponseDto update(UUID publicId, CategoryRequestDto categoryRequestDto) {
        Category category = getEntityById(categoryRequestDto.getParentCategoryId());
        category.setName(categoryRequestDto.getName());
        log.debug("[CategoryRepository] Calling save to update category {}", category.getPublicId());
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryResponseDto getById(UUID publicId) {
        Category category = getEntityById(publicId);
        return categoryMapper.toDto(category);
    }

    @Override
    public Category getEntityById(UUID publicId) {
        UUID currentFoodVenueId = tenantContext.getFoodVenueId();

        log.debug("[CategoryRepository] Calling findByPublicIdAndFoodVenue_PublicId for categoryId {} in venue {}",
                publicId, currentFoodVenueId);

        return categoryRepository.findByPublicIdAndFoodVenue_PublicIdAndDeletedFalse(publicId, currentFoodVenueId)
                .orElseThrow(() -> new EntityNotFoundException(CATEGORY));

    }

    @Override
    public void delete(UUID publicId) {
        Category category = getEntityById(publicId);
        category.setDeleted(Boolean.TRUE);
        log.debug("[CategoryRepository] Calling save to soft delete category {}", publicId);
        categoryRepository.save(category);
    }

    @Override
    public List<CategoryResponseDto> getAll() {
        UUID currentFoodVenueId = tenantContext.getFoodVenueId();

        log.debug("[CategoryRepository] Calling findByParentCategoryIsNullAndFoodVenue to get root categories for " +
                "venue {}", currentFoodVenueId);

        List<Category> roots = categoryRepository.findByParentCategoryIsNullAndFoodVenue_PublicIdAndDeletedFalse(currentFoodVenueId);

        return roots.stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public List<CategoryResponseDto> getCategoriesByParentCategoryId(UUID publicId) {
        UUID currentFoodVenueId = tenantContext.getFoodVenueId();

        log.debug("[CategoryRepository] Calling findByParentCategoryPublicIdAndFoodVenue_PublicId to get children " +
                "of {} in venue {}", publicId, currentFoodVenueId);

        List<Category> children = categoryRepository.findByParentCategoryPublicIdAndFoodVenue_PublicIdAndDeletedFalse(
                publicId, currentFoodVenueId);

        return children.stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public List<Category> findParentCategories(UUID foodVenuePublicId) {
        log.debug("[CategoryRepository] Calling findAllByFoodVenue_PublicIdAndParentCategoryIsNull " +
                "to find all parent categories for venue {}", foodVenuePublicId);

        return categoryRepository.findAllByFoodVenue_PublicIdAndParentCategoryIsNullAndDeletedFalse(foodVenuePublicId)
                .stream()
                .toList();
    }
}
