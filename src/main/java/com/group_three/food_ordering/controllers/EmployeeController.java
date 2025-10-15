package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.EmployeeRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.dto.response.PageResponse;
import com.group_three.food_ordering.utils.OnCreate;
import com.group_three.food_ordering.utils.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping(ApiPaths.EMPLOYEE_URI)
@Tag(name = "Empleados (STAFF/MANAGER)", description = "Gestión de los empleados (Staff y Manager) de un Food Venue.")
public interface EmployeeController {

    @PostMapping
    @Operation(
            summary = "Crear un nuevo empleado",
            description = "Permite registrar un nuevo empleado. Se pueden asignar los roles STAFF o MANAGER.",
            requestBody = @RequestBody(
                    description = "Datos del empleado a crear",
                    required = true,
                    // CORREGIDO: El schema ahora coincide con el DTO del método.
                    content = @Content(schema = @Schema(implementation = EmployeeRequestDto.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Empleado creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    ResponseEntity<EmploymentResponseDto> create(@Validated(OnCreate.class) @RequestBody EmployeeRequestDto dto);

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de empleo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado", content = @Content)
    })
    ResponseEntity<EmploymentResponseDto> update(
            @PathVariable UUID id,
            @Validated(OnUpdate.class) @RequestBody EmployeeRequestDto dto);

    @GetMapping("/{id}")
    @Operation(summary = "Buscar un empleado por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado encontrado"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado", content = @Content)
    })
    ResponseEntity<EmploymentResponseDto> getById(@PathVariable UUID id);

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover un empleado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Empleado eliminado"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado", content = @Content)
    })
    ResponseEntity<Void> delete(@PathVariable UUID id);

    // UNIFICADO: Se reemplazaron /actives y /laid-off por este único endpoint.
    @GetMapping
    @Operation(
            summary = "Listar empleados con filtros",
            description = "Devuelve una lista paginada de empleados, opcionalmente filtrada por estado y/o email."
    )
    ResponseEntity<PageResponse<EmploymentResponseDto>> getEmployees(
            @Parameter(description = "Filtrar por el email del usuario.")
            @RequestParam(required = false) String email,

            @Parameter(description = "Filtrar por estado. 'true' para activos, 'false' para eliminados. Si se omite, devuelve todos.")
            @RequestParam(required = false) Boolean active,

            @Parameter(hidden = true)
            Pageable pageable);
}