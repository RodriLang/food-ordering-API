package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.response.*;
import com.group_three.food_ordering.mappers.CategoryMapper;
import com.group_three.food_ordering.mappers.ProductMapper;
import com.group_three.food_ordering.models.Category;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.models.Product;
import com.group_three.food_ordering.repositories.CategoryRepository;
import com.group_three.food_ordering.repositories.FoodVenueRepository;
import com.group_three.food_ordering.repositories.ProductRepository;
import com.group_three.food_ordering.services.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final TenantContext tenantContext;
    private final FoodVenueRepository foodVenueRepository;

    @Override
    public HierarchicalMenuResponseDto getCurrentContextHierarchicalMenu() {
        FoodVenue foodVenue = tenantContext.getCurrentFoodVenue();
        if(foodVenue == null) {
            throw new IllegalStateException("No tenant context available for the current request.");
        }
        return getHierarchicalMenuByFoodVenueId(foodVenue.getId());
    }

    @Override
    public FlatMenuResponseDto getCurrentContextFlatMenu() {
        FoodVenue foodVenue = tenantContext.getCurrentFoodVenue();
        if(foodVenue == null) {
            throw new IllegalStateException("No tenant context available for the current request.");
        }
        return getFlatMenuByFoodVenueId(foodVenue.getId());
    }

    @Override
    public FlatMenuResponseDto getFlatMenuByFoodVenueId(UUID foodVenueId) {
        FoodVenue foodVenue = foodVenueRepository.findById(foodVenueId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid foodVenueId: " + foodVenueId));

         return generateFlatMenu(foodVenue);
    }

    @Override
    public HierarchicalMenuResponseDto getHierarchicalMenuByFoodVenueId(UUID foodVenueId) {
        FoodVenue foodVenue = foodVenueRepository.findById(foodVenueId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid foodVenueId: " + foodVenueId));
        return generateHierarchicalMenu(foodVenue);
    }

    private FlatMenuResponseDto generateFlatMenu(FoodVenue foodVenue) {
        List<Category> leafCategories = categoryRepository.findAll().stream()
                .filter(c -> c.getChildrenCategories() == null || c.getChildrenCategories().isEmpty())
                .toList();

        List<FlatCategoryMenuResponseDto> categories = new ArrayList<>();

        for (Category category : leafCategories) {
            List<Product> products = productRepository.findAllByFoodVenue_IdAndAvailableAndCategory(
                    foodVenue.getId(), true, category);

            List<ItemMenuResponseDto> productsDto = products.stream()
                    .map(productMapper::toItemMenuDto)
                    .toList();

            if (!productsDto.isEmpty()) {
                categories.add(
                        FlatCategoryMenuResponseDto.builder()
                                .category(category.getName())
                                .products(productsDto)
                                .build()
                );
            }
        }

        return FlatMenuResponseDto.builder()
                .foodVenueName(foodVenue.getName())
                .foodVenueImageUrl(foodVenue.getImageUrl())
                .menu(categories)
                .build();
    }


    private HierarchicalMenuResponseDto generateHierarchicalMenu(FoodVenue foodVenue) {
        // Categorías raíz (sin padre)
        List<Category> rootCategories = categoryRepository.findAll().stream()
                .filter(c -> c.getParentCategory() == null)
                .toList();

        List<HierarchicalCategoryMenuResponseDto> categoriesDto = rootCategories.stream()
                .map(root -> buildCategoryTree(root, foodVenue.getId()))
                .toList();

        return HierarchicalMenuResponseDto.builder()
                .foodVenueName(foodVenue.getName())
                .foodVenueImageUrl(foodVenue.getImageUrl())
                .menu(categoriesDto)
                .build();
    }

    /**
     * Construcción recursiva de categorías + productos
     */
    private HierarchicalCategoryMenuResponseDto buildCategoryTree(Category category, UUID foodVenueId) {
        // Productos de esta categoría
        List<Product> products = productRepository.findAllByFoodVenue_IdAndAvailableAndCategory(
                foodVenueId, true, category);

        List<ItemMenuResponseDto> productsDto = products.stream()
                .map(productMapper::toItemMenuDto)
                .toList();

        // Subcategorías (recursivo)
        List<HierarchicalCategoryMenuResponseDto> children = category.getChildrenCategories() != null
                ? category.getChildrenCategories().stream()
                .map(child -> buildCategoryTree(child, foodVenueId))
                .toList()
                : new ArrayList<>();

        return HierarchicalCategoryMenuResponseDto.builder()
                .category(category.getName())
                .products(productsDto)
                .subcategory(children)
                .build();
    }
}
