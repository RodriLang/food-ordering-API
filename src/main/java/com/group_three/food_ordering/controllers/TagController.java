package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dtos.create.CategoryCreateDto;
import com.group_three.food_ordering.dtos.create.TagCreateDto;
import com.group_three.food_ordering.dtos.response.TagResponseDto;
import com.group_three.food_ordering.dtos.update.TagUpdateDto;
import com.group_three.food_ordering.services.interfaces.ITagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.TAG_BASE)
@RequiredArgsConstructor
public class TagController {

    private final ITagService tagService;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN','ROOT')")
    @Operation(summary = "Crear una nueva etiqueta")
    @ApiResponse(responseCode = "200", description = "Etiqueta creada correctamente")
    @PostMapping
    public ResponseEntity<TagResponseDto> createTag(
            @RequestBody @Valid TagCreateDto tagCreateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(tagService.create(tagCreateDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Operation(summary = "Listar todas las etiquetas")
    @ApiResponse(responseCode = "200", description = "Listado de etiquetas")
    @GetMapping
    public ResponseEntity<List<TagResponseDto>> getAllTags() {
        return ResponseEntity.status(HttpStatus.OK).body(tagService.getAll());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Operation(summary = "Obtener una etiqueta por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Etiqueta encontrada"),
            @ApiResponse(responseCode = "404", description = "Etiqueta no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TagResponseDto> getTagById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(tagService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','ROOT')")
    @Operation(summary = "Eliminar una etiqueta")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Etiqueta eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Etiqueta no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<TagResponseDto> deleteTag(@PathVariable Long id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN','ROOT')")
    @Operation(summary = "Actualizar una etiqueta existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Etiqueta actualizada correctamente"),
            @ApiResponse(responseCode = "404", description = "Etiqueta no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TagResponseDto> updateTag(@PathVariable Long id,
                                                    @RequestBody @Valid TagCreateDto tagCreateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(tagService.update(id, tagCreateDto));
    }
}