package com.group_three.food_ordering.services.interfaces;

import com.group_three.food_ordering.dtos.response.MenuResponseDto;

import java.util.List;

public interface IMenuService {
    List<MenuResponseDto> getMenu();
}
