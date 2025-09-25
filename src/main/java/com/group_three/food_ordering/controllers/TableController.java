package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.create.TableCreateDto;
import com.group_three.food_ordering.dto.response.TableResponseDto;
import com.group_three.food_ordering.dto.update.TableUpdateDto;
import com.group_three.food_ordering.enums.TableStatus;
import com.group_three.food_ordering.services.TableService;
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
@RequestMapping(ApiPaths.TABLE_BASE)
@RequiredArgsConstructor
public class TableController {

    private final TableService tableService;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN','ROOT')")
    @Operation(
            summary = "Crear una nueva mesa",
            description = "Crea una mesa nueva en el sistema.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Mesa creada exitosamente",
                            content = @Content(schema = @Schema(implementation = TableResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
            }
    )
    @PostMapping
    public ResponseEntity<TableResponseDto> createTable(
            @RequestBody @Valid TableCreateDto tableCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tableService.create(tableCreateDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Operation(
            summary = "Obtener todas las mesas",
            description = "Devuelve una lista con todas las mesas registradas.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de mesas",
                            content = @Content(schema = @Schema(implementation = TableResponseDto.class, type = "array")))
            }
    )
    @GetMapping
    public ResponseEntity<List<TableResponseDto>> getTables() {
        return ResponseEntity.ok(tableService.getAll());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Operation(
            summary = "Obtener mesa por UUID",
            description = "Obtiene una mesa según su identificador UUID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Mesa encontrada",
                            content = @Content(schema = @Schema(implementation = TableResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Mesa no encontrada")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<TableResponseDto> getTableById(
            @Parameter(description = "UUID de la mesa a buscar", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(tableService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Operation(
            summary = "Obtener mesa por número",
            description = "Busca una mesa mediante su número único.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Mesa encontrada",
                            content = @Content(schema = @Schema(implementation = TableResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Mesa no encontrada")
            }
    )
    @GetMapping("/number/{number}")
    public ResponseEntity<TableResponseDto> getTableByNumber(
            @Parameter(description = "Número de la mesa a buscar", required = true)
            @PathVariable Integer number) {
        return ResponseEntity.ok(tableService.getByNumber(number));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Operation(
            summary = "Obtener mesas filtradas",
            description = "Filtra mesas por estado y/o capacidad. Ambos parámetros son opcionales.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de mesas filtradas",
                            content = @Content(schema = @Schema(implementation = TableResponseDto.class, type = "array")))
            }
    )
    @GetMapping("/filter")
    public ResponseEntity<List<TableResponseDto>> getFilteredTables(
            @Parameter(description = "Estado de la mesa (opcional)")
            @RequestParam(required = false) TableStatus status,
            @Parameter(description = "Capacidad de la mesa (opcional)")
            @RequestParam(required = false) Integer capacity) {
        return ResponseEntity.ok(tableService.getByFilters(status, capacity));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Operation(
            summary = "Actualizar una mesa",
            description = "Actualiza todos los datos de una mesa existente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Mesa actualizada correctamente",
                            content = @Content(schema = @Schema(implementation = TableResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos para actualización"),
                    @ApiResponse(responseCode = "404", description = "Mesa no encontrada")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<TableResponseDto> update(
            @RequestBody @Valid TableUpdateDto tableUpdateDto,
            @Parameter(description = "UUID de la mesa a actualizar", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(tableService.update(tableUpdateDto, id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Operation(
            summary = "Modificar parcialmente una mesa",
            description = "Modifica parcialmente los datos de una mesa.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Mesa modificada correctamente",
                            content = @Content(schema = @Schema(implementation = TableResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos para modificación"),
                    @ApiResponse(responseCode = "404", description = "Mesa no encontrada")
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<TableResponseDto> patch(
            @RequestBody @Valid TableUpdateDto tableUpdateDto,
            @Parameter(description = "UUID de la mesa a modificar", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(tableService.update(tableUpdateDto, id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN','ROOT')")
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
        tableService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
