package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.MenuController;
import com.group_three.food_ordering.dto.response.MenuResponseDto;
import com.group_three.food_ordering.services.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MenuControllerImpl implements MenuController {

    private final MenuService menuService;

    @Override
    public ResponseEntity<MenuResponseDto> getHierarchicalMenu(String category) {
        return ResponseEntity.ok(menuService.getCurrentContextHierarchicalMenu(category));
    }
}
