package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.utils.constants.ApiPaths;
import com.group_three.food_ordering.dto.request.TagRequestDto;
import com.group_three.food_ordering.dto.response.TagResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(ApiPaths.TAG_URI)
@Tag(name = "Etiquetas", description = "Gestión de etiquetas para identificadores no estructurados de los productos")
public interface TagController {

    @Operation(summary = "Crear una nueva etiqueta")
    @ApiResponse(responseCode = "200", description = "Etiqueta creada correctamente")
    @PostMapping
    ResponseEntity<TagResponseDto> createTag(
            @RequestBody @Valid TagRequestDto tagRequestDto);


    @Operation(summary = "Listar todas las etiquetas")
    @ApiResponse(responseCode = "200", description = "Listado de etiquetas")
    @GetMapping
    ResponseEntity<List<TagResponseDto>> getAllTags();

}