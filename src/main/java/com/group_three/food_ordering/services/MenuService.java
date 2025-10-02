package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.response.FlatMenuResponseDto;
import com.group_three.food_ordering.dto.response.HierarchicalMenuResponseDto;

import java.util.UUID;

public interface MenuService {

    HierarchicalMenuResponseDto getCurrentContextHierarchicalMenu();

    HierarchicalMenuResponseDto getHierarchicalMenuByFoodVenueId(UUID foodVenueId);

    FlatMenuResponseDto getCurrentContextFlatMenu();

    FlatMenuResponseDto getFlatMenuByFoodVenueId(UUID foodVenueId);
}
