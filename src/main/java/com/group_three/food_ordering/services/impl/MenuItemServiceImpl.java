package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dtos.create.MenuItemCreateDto;
import com.group_three.food_ordering.dtos.response.MenuItemResponseDto;
import com.group_three.food_ordering.dtos.update.MenuItemUpdateDto;
import com.group_three.food_ordering.repositories.IMenuItemRepository;
import com.group_three.food_ordering.services.interfaces.IMenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MenuItemServiceImpl implements IMenuItemService {

    private final IMenuItemRepository menuItemRepository;

    @Override
    public MenuItemResponseDto create(MenuItemCreateDto menuItemCreateDto) {
        return null;
    }

    @Override
    public List<MenuItemResponseDto> getAll() {
        return List.of();
    }

    @Override
    public MenuItemResponseDto getById(Long id) {
        return null;
    }

    @Override
    public MenuItemResponseDto update(MenuItemUpdateDto menuItemUpdateDto) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
