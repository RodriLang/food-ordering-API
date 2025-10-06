package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.DiningTableRequestDto;
import com.group_three.food_ordering.dto.response.DiningTableResponseDto;
import com.group_three.food_ordering.enums.DiningTableStatus;
import com.group_three.food_ordering.services.DiningTableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.TABLE_URI)
@RequiredArgsConstructor
public class DiningTableController {

    private final DiningTableService diningTableService;

    @PreAuthorize("hasAnyRole('ADMIN','ROOT')")
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
    public ResponseEntity<DiningTableResponseDto> createTable(
            @RequestBody @Valid DiningTableRequestDto diningTableRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(diningTableService.create(diningTableRequestDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER','ROOT')")
    @Operation(
            summary = "Obtener todas las mesas",
            description = "Devuelve una lista con todas las mesas registradas.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de mesas",
                            content = @Content(schema = @Schema(implementation = DiningTableResponseDto.class, type = "array")))
            }
    )
    @GetMapping
    public ResponseEntity<List<DiningTableResponseDto>> getTables() {
        return ResponseEntity.ok(diningTableService.getAll());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER','ROOT')")
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
    public ResponseEntity<DiningTableResponseDto> getTableById(
            @Parameter(description = "UUID de la mesa a buscar", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(diningTableService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER','ROOT')")
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
    public ResponseEntity<DiningTableResponseDto> getTableByNumber(
            @Parameter(description = "Número de la mesa a buscar", required = true)
            @PathVariable Integer number) {
        return ResponseEntity.ok(diningTableService.getByNumber(number));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER','ROOT')")
    @Operation(
            summary = "Obtener mesas filtradas",
            description = "Filtra mesas por estado y/o capacidad. Ambos parámetros son opcionales.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de mesas filtradas",
                            content = @Content(schema = @Schema(implementation = DiningTableResponseDto.class, type = "array")))
            }
    )
    @GetMapping("/filter")
    public ResponseEntity<List<DiningTableResponseDto>> getFilteredTables(
            @Parameter(description = "Estado de la mesa (opcional)")
            @RequestParam(required = false) DiningTableStatus status,
            @Parameter(description = "Capacidad de la mesa (opcional)")
            @RequestParam(required = false) Integer capacity) {
        return ResponseEntity.ok(diningTableService.getByFilters(status, capacity));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER','ROOT')")
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
    public ResponseEntity<Void> update(
            @RequestParam @Valid DiningTableStatus status,
            @Parameter(description = "UUID de la mesa a actualizar", required = true)
            @PathVariable UUID id) {
        diningTableService.updateStatus(status, id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER','ROOT')")
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
    public ResponseEntity<DiningTableResponseDto> patch(
            @RequestBody @Valid DiningTableRequestDto diningTableRequestDto,
            @Parameter(description = "UUID de la mesa a modificar", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(diningTableService.update(diningTableRequestDto, id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','ROOT')")
    @Operation(
            summary = "Eliminar una mesa",
            description = "Elimina una mesa del sistema usando su UUID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Mesa eliminada exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Mesa no encontrada")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "UUID de la mesa a eliminar", required = true)
            @PathVariable UUID id) {
        diningTableService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
