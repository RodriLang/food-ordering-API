package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dtos.create.EmployeeCreateDto;
import com.group_three.food_ordering.dtos.response.EmployeeResponseDto;
import com.group_three.food_ordering.dtos.update.EmployeePatchDto;
import com.group_three.food_ordering.dtos.update.EmployeeUpdateDto;
import com.group_three.food_ordering.services.interfaces.IEmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.EMPLOYEE_BASE)
@RequiredArgsConstructor
public class EmployeeController {

    private final IEmployeeService employeeService;


    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN','ROOT')")
    @PostMapping
    @Operation(
            summary = "Crear un nuevo empleado",
            description = "Permite registrar un nuevo empleado con usuario completo. Se asigna automáticamente el rol ROLE_STAFF.",
            requestBody = @RequestBody(
                    description = "Datos del empleado a crear",
                    required = true,
                    content = @Content(schema = @Schema(implementation = EmployeeCreateDto.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Empleado creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    public ResponseEntity<EmployeeResponseDto> create(@Valid @org.springframework.web.bind.annotation.RequestBody EmployeeCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar completamente un empleado",
            description = "Actualiza todos los campos del empleado especificado por ID. No se puede modificar el email."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado", content = @Content)
    })
    public ResponseEntity<EmployeeResponseDto> update(
            @PathVariable UUID id,
            @Valid @org.springframework.web.bind.annotation.RequestBody EmployeeUpdateDto dto) {
        return ResponseEntity.ok(employeeService.update(id, dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @PatchMapping("/{id}")
    @Operation(
            summary = "Actualizar parcialmente un empleado",
            description = "Permite modificar parcialmente los campos del empleado (como nombre, teléfono, posición, etc.).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Empleado actualizado parcialmente"),
                    @ApiResponse(responseCode = "404", description = "Empleado no encontrado", content = @Content)
            }
    )
    public ResponseEntity<EmployeeResponseDto> partialUpdate(
            @PathVariable UUID id,
            @org.springframework.web.bind.annotation.RequestBody EmployeePatchDto dto) {
        return ResponseEntity.ok(employeeService.partialUpdate(id, dto));
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @GetMapping("/all")
    @Operation(
            summary = "Listar todos los empleados",
            description = "Devuelve todos los empleados activos (user.removedAt == null)."
    )
    public ResponseEntity<List<EmployeeResponseDto>> getAll() {
        return ResponseEntity.ok(employeeService.getAll());
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar un empleado por ID",
            description = "Devuelve un empleado específico si está activo."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado encontrado"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado", content = @Content)
    })
    public ResponseEntity<EmployeeResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','ROOT')")
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un empleado",
            description = "Marca como eliminado al usuario asociado (setea removedAt)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Empleado eliminado"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado", content = @Content)
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @GetMapping("/actives")
    @Operation(
            summary = "Listar empleados activos",
            description = "Devuelve todos los empleados que no han sido eliminados (removedAt es null en su usuario)."
    )
    public ResponseEntity<List<EmployeeResponseDto>> getActiveEmployees() {
        List<EmployeeResponseDto> actives = employeeService.getAll().stream()
                .filter(e -> e.getUser() == null || e.getUser().getRemovedAt() == null)
                .toList();
        return ResponseEntity.ok(actives);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN','ROOT')")
    @GetMapping("/deleted")
    @Operation(
            summary = "Listar empleados eliminados",
            description = "Devuelve todos los empleados cuyo usuario fue eliminado (removedAt no es null)."
    )
    public ResponseEntity<List<EmployeeResponseDto>> getDeletedEmployees() {
        List<EmployeeResponseDto> deleted = employeeService.getAll().stream()
                .filter(e -> e.getUser() != null && e.getUser().getRemovedAt() != null)
                .toList();
        return ResponseEntity.ok(deleted);
    }
}
