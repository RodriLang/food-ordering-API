package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.response.MenuResponseDto;

import java.util.UUID;

public interface MenuService {

    MenuResponseDto getCurrentContextHierarchicalMenu(String category);

    MenuResponseDto getHierarchicalMenuByFoodVenueId(UUID foodVenueId, String category);

}
