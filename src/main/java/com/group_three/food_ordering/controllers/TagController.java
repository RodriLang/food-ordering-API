package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.TagRequestDto;
import com.group_three.food_ordering.dto.response.TagResponseDto;
import com.group_three.food_ordering.services.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.TAG_URI)
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN','ROOT')")
    @Operation(summary = "Crear una nueva etiqueta")
    @ApiResponse(responseCode = "200", description = "Etiqueta creada correctamente")
    @PostMapping
    public ResponseEntity<TagResponseDto> createTag(
            @RequestBody @Valid TagRequestDto tagRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.create(tagRequestDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Operation(summary = "Listar todas las etiquetas")
    @ApiResponse(responseCode = "200", description = "Listado de etiquetas")
    @GetMapping
    public ResponseEntity<List<TagResponseDto>> getAllTags() {
        return ResponseEntity.status(HttpStatus.OK).body(tagService.getAll());
    }
}