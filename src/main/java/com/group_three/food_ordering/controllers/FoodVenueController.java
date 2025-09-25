package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.FoodVenueRequestDto;
import com.group_three.food_ordering.dto.response.FoodVenueAdminResponseDto;
import com.group_three.food_ordering.dto.response.FoodVenuePublicResponseDto;
import com.group_three.food_ordering.services.FoodVenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.VENUE_BASE)
@RequiredArgsConstructor
public class FoodVenueController {

    private final FoodVenueService foodVenueService;

    @PreAuthorize("hasRole('ROOT')")
    @Operation(
            summary = "Crear un nuevo lugar de comida",
            description = "Crea un nuevo lugar de comida. Solo usuarios con rol root pueden crear un lugar.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Lugar de comida creado correctamente",
                            content = @Content(schema = @Schema(implementation = FoodVenuePublicResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado")
            }
    )
    @PostMapping
    public ResponseEntity<FoodVenueAdminResponseDto> createFoodVenue(
            @RequestBody @Valid FoodVenueRequestDto foodVenueRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(foodVenueService.create(foodVenueRequestDto));
    }

    @PreAuthorize("hasRole('ROOT')")
    @Operation(
            summary = "Obtener todos los lugares de comida",
            description = "Devuelve la lista completa de lugares de comida. Solo usuarios con rol root pueden acceder.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de lugares de comida",
                            content = @Content(schema = @Schema(implementation = FoodVenuePublicResponseDto.class, type = "array"))),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado")
            }
    )
    @GetMapping
    public ResponseEntity<Page<FoodVenueAdminResponseDto>> getFoodVenues(@Parameter Pageable pageable) {
        return ResponseEntity.ok(foodVenueService.getAll(pageable));
    }

    @PreAuthorize("hasRole('ROOT')")
    @Operation(
            summary = "Obtener los lugares de comida eliminados",
            description = "Devuelve la lista de lugares de comida que han sido borrados de forma lógica. Solo usuarios con rol root pueden acceder.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de lugares de comida",
                            content = @Content(schema = @Schema(implementation = FoodVenuePublicResponseDto.class, type = "array"))),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado")
            }
    )
    @GetMapping("/deleted")
    public ResponseEntity<Page<FoodVenueAdminResponseDto>> getDeletedFoodVenues(@Parameter Pageable pageable) {
        return ResponseEntity.ok(foodVenueService.getDeleted(pageable));
    }

    @PreAuthorize("hasRole('ROOT')")
    @Operation(
            summary = "Obtener un lugar de comida por ID",
            description = "Devuelve un lugar de comida identificado por su UUID. Accesible para roles root.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lugar de comida encontrado",
                            content = @Content(schema = @Schema(implementation = FoodVenuePublicResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Lugar de comida no encontrado"),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<FoodVenueAdminResponseDto> getFoodVenueById(
            @Parameter(description = "UUID del lugar de comida a obtener", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(foodVenueService.getById(id));
    }

    @PreAuthorize("hasRole('ROOT')")
    @Operation(
            summary = "Actualizar un lugar de comida por ID",
            description = "Actualiza un lugar de comida identificado por su UUID. Accesible para roles root.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lugar de comida actualizado correctamente",
                            content = @Content(schema = @Schema(implementation = FoodVenuePublicResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos"),
                    @ApiResponse(responseCode = "404", description = "Lugar de comida no encontrado"),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado")
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<FoodVenueAdminResponseDto> patch(
            @PathVariable UUID id,
            @RequestBody @Valid FoodVenueRequestDto foodVenueRequestDto) {
        return ResponseEntity.ok(foodVenueService.update(id, foodVenueRequestDto));
    }

    @PreAuthorize("hasRole('ROOT')")
    @Operation(
            summary = "Eliminar un lugar de comida",
            description = "Elimina un lugar de comida por su UUID. Accesible para root.",
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
        foodVenueService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('GUEST', 'CLIENT', 'STAFF', 'ADMIN', 'SUPER_ADMIN')")
    @Operation(
            summary = "Obtener el lugar de comida",
            description = "Devuelve el lugar de comida asociado al usuario, o en que tenga una mesa activa.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lugar de comida encontrado",
                            content = @Content(schema = @Schema(implementation = FoodVenuePublicResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Lugar de comida no encontrado"),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado")
            }
    )
    @GetMapping("/current")
    public ResponseEntity<FoodVenuePublicResponseDto> getMyCurrentFoodVenue() {
        return ResponseEntity.ok(foodVenueService.getMyCurrentFoodVenue());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(
            summary = "Actualizar un lugar de comida",
            description = "Actualiza el lugar de comida asociado al usuario registrado. accesible para admin",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lugar de comida actualizado correctamente",
                            content = @Content(schema = @Schema(implementation = FoodVenuePublicResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos"),
                    @ApiResponse(responseCode = "404", description = "Lugar de comida no encontrado"),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado")
            }
    )
    @PatchMapping("/current")
    public ResponseEntity<FoodVenuePublicResponseDto> updateMyCurrentFoodVenue(
            @RequestBody @Valid FoodVenueRequestDto foodVenueRequestDto) {
        return ResponseEntity.ok(foodVenueService.updateMyCurrentFoodVenue(foodVenueRequestDto));
    }
}
