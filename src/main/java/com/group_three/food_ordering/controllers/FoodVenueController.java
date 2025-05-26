package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dtos.create.FoodVenueCreateDto;
import com.group_three.food_ordering.dtos.response.FoodVenueResponseDto;
import com.group_three.food_ordering.dtos.update.FoodVenueUpdateDto;
import com.group_three.food_ordering.services.interfaces.IFoodVenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.VENUE_BASE)
@RequiredArgsConstructor
public class FoodVenueController {

    private final IFoodVenueService foodVenueService;

    @Operation(
            summary = "Crear un nuevo lugar de comida",
            description = "Crea un nuevo lugar de comida. Solo usuarios con rol root pueden crear un lugar.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Lugar de comida creado correctamente",
                            content = @Content(schema = @Schema(implementation = FoodVenueResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado")
            }
    )
    @PostMapping
    public ResponseEntity<FoodVenueResponseDto> createFoodVenue(
            @RequestBody @Valid FoodVenueCreateDto foodVenueCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(foodVenueService.create(foodVenueCreateDto));
    }

    @Operation(
            summary = "Obtener todos los lugares de comida",
            description = "Devuelve la lista completa de lugares de comida. Solo usuarios con rol root pueden acceder.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de lugares de comida",
                            content = @Content(schema = @Schema(implementation = FoodVenueResponseDto.class, type = "array"))),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado")
            }
    )
    @GetMapping
    public ResponseEntity<List<FoodVenueResponseDto>> getFoodVenues() {
        return ResponseEntity.ok(foodVenueService.getAll());
    }

    @Operation(
            summary = "Obtener un lugar de comida por ID",
            description = "Devuelve un lugar de comida identificado por su UUID. Accesible para roles admin y root.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lugar de comida encontrado",
                            content = @Content(schema = @Schema(implementation = FoodVenueResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Lugar de comida no encontrado"),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<FoodVenueResponseDto> getFoodVenueById(
            @Parameter(description = "UUID del lugar de comida a obtener", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(foodVenueService.getById(id));
    }

    @Operation(
            summary = "Actualizar un lugar de comida",
            description = "Actualiza un lugar de comida existente. Admin puede actualizar cualquiera; root solo el propio.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lugar de comida actualizado correctamente",
                            content = @Content(schema = @Schema(implementation = FoodVenueResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos"),
                    @ApiResponse(responseCode = "404", description = "Lugar de comida no encontrado"),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<FoodVenueResponseDto> update(
            @RequestBody @Valid FoodVenueUpdateDto foodVenueUpdateDto) {
        return ResponseEntity.ok(foodVenueService.update(foodVenueUpdateDto));
    }

    @Operation(
            summary = "Modificar parcialmente un lugar de comida",
            description = "Actualiza parcialmente un lugar de comida. Permisos idénticos al método PUT.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lugar de comida modificado correctamente",
                            content = @Content(schema = @Schema(implementation = FoodVenueResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos"),
                    @ApiResponse(responseCode = "404", description = "Lugar de comida no encontrado"),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado")
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<FoodVenueResponseDto> patch(
            @RequestBody @Valid FoodVenueUpdateDto foodVenueUpdateDto) {
        return ResponseEntity.ok(foodVenueService.update(foodVenueUpdateDto));
    }

    @Operation(
            summary = "Eliminar un lugar de comida",
            description = "Elimina un lugar de comida por su UUID. Admin puede eliminar cualquiera; root solo el propio.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Lugar de comida eliminado correctamente"),
                    @ApiResponse(responseCode = "404", description = "Lugar de comida no encontrado"),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoodVenue(
            @Parameter(description = "UUID del lugar de comida a eliminar", required = true)
            @PathVariable UUID id) {
        foodVenueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
