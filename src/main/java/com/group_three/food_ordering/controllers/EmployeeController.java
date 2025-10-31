package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.EmployeeRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.dto.response.PageResponse;
import com.group_three.food_ordering.utils.OnCreate;
import com.group_three.food_ordering.utils.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(summary = "Crear un nuevo empleado (STAFF/MANAGER)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Empleado creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    ResponseEntity<EmploymentResponseDto> create(
            @Validated(OnCreate.class) @RequestBody EmployeeRequestDto dto);

    @Operation(summary = "Actualizar datos de un empleado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Empleado actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    })
    @PutMapping("/{id}")
    ResponseEntity<EmploymentResponseDto> update(
            @PathVariable UUID id,
            @Validated(OnUpdate.class) @RequestBody EmployeeRequestDto dto);

    @Operation(summary = "Buscar un empleado por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Empleado encontrado"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    })
    @GetMapping("/{id}")
    ResponseEntity<EmploymentResponseDto> getById(@PathVariable UUID id);

    @Operation(summary = "Eliminar (desactivar) un empleado")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Empleado eliminado"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id);

    @Operation(summary = "Listar empleados con filtros")
    @ApiResponse(responseCode = "200", description = "Listado de empleados")
    @GetMapping
    ResponseEntity<PageResponse<EmploymentResponseDto>> getEmployees(
            @Parameter(description = "Filtrar por el email del usuario.")
            @RequestParam(required = false) String email,

            @Parameter(description = "Filtrar por estado. 'true' para activos, 'false' para eliminados. Si se omite, devuelve todos.")
            @RequestParam(required = false) Boolean active,

            @Parameter(hidden = true)
            Pageable pageable);

    @Operation(summary = "Verificar si ya existe un usuario con un email específico")
    @ApiResponse(responseCode = "200", description = "Devuelve 'true' si el email existe, 'false' si no.")
    @GetMapping("/exists-by-email")
    ResponseEntity<Boolean> existsByEmail(
            @Parameter(description = "El email a verificar.")
            @RequestParam String email);

}