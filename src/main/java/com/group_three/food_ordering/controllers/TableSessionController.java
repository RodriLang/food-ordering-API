package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dtos.create.TableSessionCreateDto;
import com.group_three.food_ordering.dtos.response.TableSessionResponseDto;
import com.group_three.food_ordering.services.interfaces.ITableSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.TABLE_SESSION_BASE)
@RequiredArgsConstructor
public class TableSessionController {

    private final ITableSessionService tableSessionService;

    @PostMapping
    public ResponseEntity<TableSessionResponseDto> createTableSession(@RequestBody @Valid TableSessionCreateDto tableSessionCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tableSessionService.create(tableSessionCreateDto));
    }
}
