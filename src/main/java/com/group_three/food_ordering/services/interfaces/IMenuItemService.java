package com.group_three.food_ordering.services.interfaces;

import com.group_three.food_ordering.dtos.response.MenuItemResponseDto;
import com.group_three.food_ordering.dtos.create.MenuItemCreateDto;
import com.group_three.food_ordering.dtos.update.MenuItemUpdateDto;

import java.util.List;

public interface IMenuItemService {

    MenuItemResponseDto create(MenuItemCreateDto menuItemCreateDto);
    List<MenuItemResponseDto> getAll();
    MenuItemResponseDto getById(Long id);
    MenuItemResponseDto update(MenuItemUpdateDto menuItemUpdateDto);
    void delete(Long id);
}
