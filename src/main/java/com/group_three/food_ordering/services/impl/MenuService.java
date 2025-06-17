package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dtos.response.MenuResponseDto;
import com.group_three.food_ordering.dtos.response.ProductResponseDto;
import com.group_three.food_ordering.mappers.ProductMapper;
import com.group_three.food_ordering.models.Category;
import com.group_three.food_ordering.models.Product;
import com.group_three.food_ordering.repositories.ICategoryRepository;
import com.group_three.food_ordering.repositories.IProductRepository;
import com.group_three.food_ordering.services.interfaces.IMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MenuService implements IMenuService {

    private final ICategoryRepository categoryRepository;
    private final IProductRepository productRepository;
    private final ProductMapper productMapper;
    private final TenantContext tenantContext;

    @Override
    public List<MenuResponseDto> getMenu() {

        List<Category> leafCategories = categoryRepository.findAll().stream()
                .filter(c -> c.getChildrenCategories() == null || c.getChildrenCategories().isEmpty())
                .toList();

        List<MenuResponseDto> menu = new ArrayList<>();

        for (Category category : leafCategories) {
            List<Product> products = productRepository.findAllByFoodVenue_IdAndAvailableAndCategory(
                    tenantContext.getCurrentFoodVenue().getId(),true, category);

            List<ProductResponseDto> productDtos = products.stream()
                    .map(productMapper::toDTO)
                    .toList();

            if (!productDtos.isEmpty()) {
                menu.add(
                        MenuResponseDto.builder()
                                .categoryName(category.getName())
                                .products(productDtos)
                                .build()
                );
            }
        }

        return menu;
    }
}
