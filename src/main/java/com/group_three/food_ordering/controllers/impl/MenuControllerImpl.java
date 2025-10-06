package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.MenuController;
import com.group_three.food_ordering.dto.response.FlatMenuResponseDto;
import com.group_three.food_ordering.dto.response.HierarchicalMenuResponseDto;
import com.group_three.food_ordering.services.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class MenuControllerImpl implements MenuController {

    private final MenuService menuService;

    @Override
    public ResponseEntity<FlatMenuResponseDto> getFlatMenu() {
        return ResponseEntity.ok(menuService.getCurrentContextFlatMenu());
    }

    @Override
    public ResponseEntity<HierarchicalMenuResponseDto> getHierarchicalMenu(String category) {
        return ResponseEntity.ok(menuService.getCurrentContextHierarchicalMenu(category));
    }
}
