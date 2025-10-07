package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.DiningTableRequestDto;
import com.group_three.food_ordering.dto.response.DiningTableResponseDto;
import com.group_three.food_ordering.enums.DiningTableStatus;
import com.group_three.food_ordering.utils.OnCreate;
import com.group_three.food_ordering.utils.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping(ApiPaths.TABLE_URI)
@Tag(name = "Mesas", description = "Gestión de las mesas de los lugares de comida")
public interface DiningTableController {

    @Operation(
            summary = "Crear una nueva mesa",
            description = "Crea una mesa nueva en el sistema.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Mesa creada exitosamente",
                            content = @Content(schema = @Schema(implementation = DiningTableResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
            }
    )
    @PostMapping
    ResponseEntity<DiningTableResponseDto> createTable(
            @RequestBody @Validated(OnCreate.class) DiningTableRequestDto diningTableRequestDto);


    @Operation(
            summary = "Obtener todas las mesas",
            description = "Devuelve una lista con todas las mesas registradas.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de mesas",
                            content = @Content(schema = @Schema(implementation = DiningTableResponseDto.class, type = "array")))
            }
    )
    @GetMapping
    ResponseEntity<Page<DiningTableResponseDto>> getTables(@Parameter(hidden = true) Pageable pageable);


    @Operation(
            summary = "Obtener mesa por UUID",
            description = "Obtiene una mesa según su identificador UUID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Mesa encontrada",
                            content = @Content(schema = @Schema(implementation = DiningTableResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Mesa no encontrada")
            }
    )
    @GetMapping("/{id}")
    ResponseEntity<DiningTableResponseDto> getTableById(
            @Parameter(description = "UUID de la mesa a buscar", required = true)
            @PathVariable UUID id);


    @Operation(
            summary = "Obtener mesa por número",
            description = "Busca una mesa mediante su número único.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Mesa encontrada",
                            content = @Content(schema = @Schema(implementation = DiningTableResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Mesa no encontrada")
            }
    )
    @GetMapping("/number/{number}")
    ResponseEntity<DiningTableResponseDto> getTableByNumber(
            @Parameter(description = "Número de la mesa a buscar", required = true)
            @PathVariable Integer number);


    @Operation(
            summary = "Obtener mesas filtradas",
            description = "Filtra mesas por estado y/o capacidad. Ambos parámetros son opcionales.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de mesas filtradas",
                            content = @Content(schema = @Schema(implementation = DiningTableResponseDto.class, type = "array")))
            }
    )
    @GetMapping("/filter")
    ResponseEntity<Page<DiningTableResponseDto>> getFilteredTables(
            @Parameter(description = "Estado de la mesa (opcional)")
            @RequestParam(required = false) DiningTableStatus status,
            @Parameter(description = "Capacidad de la mesa (opcional)")
            @RequestParam(required = false) Integer capacity,
            @Parameter(hidden = true) Pageable pageable);


    @Operation(
            summary = "Actualizar estado de una mesa",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Mesa actualizada correctamente",
                            content = @Content(schema = @Schema(implementation = DiningTableResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos para actualización"),
                    @ApiResponse(responseCode = "404", description = "Mesa no encontrada")
            }
    )
    @PatchMapping("/status/{id}")
    ResponseEntity<Void> update(
            @RequestParam @Validated(OnUpdate.class) DiningTableStatus status,
            @Parameter(description = "UUID de la mesa a actualizar", required = true)
            @PathVariable UUID id);


    @Operation(
            summary = "Modificar parcialmente una mesa",
            description = "Modifica parcialmente los datos de una mesa.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Mesa modificada correctamente",
                            content = @Content(schema = @Schema(implementation = DiningTableResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos para modificación"),
                    @ApiResponse(responseCode = "404", description = "Mesa no encontrada")
            }
    )
    @PatchMapping("/{id}")
    ResponseEntity<DiningTableResponseDto> patch(
            @RequestBody @Valid DiningTableRequestDto diningTableRequestDto,
            @Parameter(description = "UUID de la mesa a modificar", required = true)
            @PathVariable UUID id);


    @Operation(
            summary = "Eliminar una mesa",
            description = "Elimina una mesa del sistema usando su UUID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Mesa eliminada exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Mesa no encontrada")
            }
    )
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(
            @Parameter(description = "UUID de la mesa a eliminar", required = true)
            @PathVariable UUID id);

}
