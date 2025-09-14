package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.response.MenuResponseDto;

import java.util.List;

public interface MenuService {
    List<MenuResponseDto> getMenu();
}
