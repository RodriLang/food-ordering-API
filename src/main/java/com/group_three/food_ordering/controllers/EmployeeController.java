package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.EmployeeRequestDto;
import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.services.EmploymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping(ApiPaths.EMPLOYMENT_URI)
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ROOT')")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmploymentService employmentService;


    @PostMapping
    @Operation(
            summary = "Crear un nuevo empleado",
            description = "Permite registrar un nuevo empleado. Se asigna automáticamente el rol ROLE_STAFF.",
            requestBody = @RequestBody(
                    description = "Datos del empleado a crear",
                    required = true,
                    content = @Content(schema = @Schema(implementation = EmploymentRequestDto.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Empleado creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    public ResponseEntity<EmploymentResponseDto> create(@Valid @org.springframework.web.bind.annotation.RequestBody EmployeeRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employmentService.createEmployment(dto));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar completamente un empleado",
            description = "Actualiza todos los campos del empleado especificado por ID. No se puede modificar el email."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado", content = @Content)
    })
    public ResponseEntity<EmploymentResponseDto> update(
            @PathVariable UUID id,
            @Valid @org.springframework.web.bind.annotation.RequestBody EmploymentRequestDto dto) {
        return ResponseEntity.ok(employmentService.update(id, dto));
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar un empleado por ID",
            description = "Devuelve un empleado específico si está activo."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado encontrado"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado", content = @Content)
    })
    public ResponseEntity<EmploymentResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(employmentService.getByIdAndActiveTrue(id));
    }

    @GetMapping("/user")
    @Operation(
            summary = "Listar empleados activos",
            description = "Devuelve todos los empleados que no han sido eliminados (removedAt es null en su usuario)."
    )
    public ResponseEntity<Page<EmploymentResponseDto>> getEmploymentsByUser(String email, Pageable pageable) {

        return ResponseEntity.ok(employmentService.getByUserAndActiveTrue(email, pageable));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un empleado",
            description = "Marca como eliminado al usuario asociado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Empleado eliminado"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado", content = @Content)
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        employmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/actives")
    @Operation(
            summary = "Listar empleados activos",
            description = "Devuelve todos los empleados que no han sido eliminados."
    )
    public ResponseEntity<Page<EmploymentResponseDto>> getActiveEmployees(Pageable pageable) {

        return ResponseEntity.ok(employmentService.getAllAndActiveTrue(pageable));
    }

    @GetMapping("/deleted")
    @Operation(
            summary = "Listar empleados eliminados",
            description = "Devuelve todos los empleados cuyo usuario fue eliminado."
    )
    public ResponseEntity<Page<EmploymentResponseDto>> getDeletedEmployees(Pageable pageable) {
        return ResponseEntity.ok(employmentService.getAllAndActiveFalse(pageable));
    }
}
