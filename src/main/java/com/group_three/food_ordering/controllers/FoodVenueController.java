package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.utils.constants.ApiPaths;
import com.group_three.food_ordering.dto.request.FoodVenueRequestDto;
import com.group_three.food_ordering.dto.response.FoodVenueAdminResponseDto;
import com.group_three.food_ordering.dto.response.FoodVenuePublicResponseDto;
import com.group_three.food_ordering.dto.response.PageResponse;
import com.group_three.food_ordering.utils.OnCreate;
import com.group_three.food_ordering.utils.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping(ApiPaths.VENUE_URI)
@Tag(name = "Lugares de comida", description = "Gestión de los lugares de comida")
public interface FoodVenueController {

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
    ResponseEntity<FoodVenueAdminResponseDto> createFoodVenue(
            @RequestBody @Validated(OnCreate.class) FoodVenueRequestDto foodVenueRequestDto);

    @Operation(
            summary = "Obtener todos los lugares de comida",
            description = "Devuelve la lista completa de lugares de comida. Solo usuarios con rol root pueden acceder.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de lugares de comida", content = @Content(
                            schema = @Schema(implementation = FoodVenuePublicResponseDto.class, type = "array"))),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado")
            }
    )
    @GetMapping("/root")
    ResponseEntity<PageResponse<FoodVenueAdminResponseDto>> getFoodVenues(@Parameter Pageable pageable);

    @Operation(
            summary = "Obtener los lugares de comida eliminados",
            description = "Devuelve la lista de lugares de comida que han sido borrados de forma lógica. Solo usuarios con rol root pueden acceder.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de lugares de comida",
                            content = @Content(schema = @Schema(implementation = FoodVenuePublicResponseDto.class, type = "array"))),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado")
            }
    )
    @GetMapping("/root/deleted")
    ResponseEntity<PageResponse<FoodVenueAdminResponseDto>> getDeletedFoodVenues(@Parameter Pageable pageable);

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
    @GetMapping("/root/{id}")
    ResponseEntity<FoodVenueAdminResponseDto> getFoodVenueById(
            @Parameter(description = "UUID del lugar de comida a obtener", required = true)
            @PathVariable UUID id);

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
    @PatchMapping("/root/{id}")
    ResponseEntity<FoodVenueAdminResponseDto> updateById(
            @PathVariable UUID id,
            @RequestBody @Validated(OnUpdate.class) FoodVenueRequestDto foodVenueRequestDto);

    @Operation(
            summary = "Eliminar un lugar de comida por ID",
            description = "Elimina un lugar de comida por su UUID. Accesible para root.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Lugar de comida eliminado correctamente"),
                    @ApiResponse(responseCode = "404", description = "Lugar de comida no encontrado"),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado")
            }
    )
    @DeleteMapping("/root/{id}")
    ResponseEntity<Void> deleteFoodVenue(
            @Parameter(description = "UUID del lugar de comida a eliminar", required = true)
            @PathVariable UUID id);


    @Operation(
            summary = "Obtener el lugar de comida actual",
            description = "Devuelve el lugar de comida asociado al usuario, o en que tenga una mesa activa.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lugar de comida encontrado",
                            content = @Content(schema = @Schema(implementation = FoodVenuePublicResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Lugar de comida no encontrado"),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado")
            }
    )
    @GetMapping("/current")
    ResponseEntity<FoodVenuePublicResponseDto> getMyCurrentFoodVenue();

    @Operation(
            summary = "Obtener el lugar de comida del admin asociado",
            description = "Devuelve el lugar de comida asociado al admin registrado. accesible para admin",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lugar de comida encontrado",
                            content = @Content(schema = @Schema(implementation = FoodVenuePublicResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Lugar de comida no encontrado"),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado")
            }
    )
    @GetMapping("/admin/current")
    ResponseEntity<FoodVenueAdminResponseDto> getMyFoodVenue();




    @Operation(
            summary = "Actualizar el lugar de comida actual",
            description = "Actualiza el lugar de comida asociado al usuario registrado. accesible para admin",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lugar de comida actualizado correctamente",
                            content = @Content(schema = @Schema(implementation = FoodVenuePublicResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos"),
                    @ApiResponse(responseCode = "404", description = "Lugar de comida no encontrado"),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado")
            }
    )
    @PatchMapping("/admin/current")
    ResponseEntity<FoodVenueAdminResponseDto> updateMyCurrentFoodVenue(
            @RequestBody @Validated(OnUpdate.class) FoodVenueRequestDto foodVenueRequestDto);
}
