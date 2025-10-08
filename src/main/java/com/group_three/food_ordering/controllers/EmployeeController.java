package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
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
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping(ApiPaths.EMPLOYMENT_URI)
@Tag(name = "Empleados", description = "Gestión de los empleados de los lugares de comida")
public interface EmployeeController {

    @PostMapping
    @Operation(
            summary = "Crear un nuevo empleado",
            description = "Permite registrar un nuevo empleado. Se pueden asignar los roles STAFF o MANAGER.",
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
    ResponseEntity<EmploymentResponseDto> create(@Validated(OnCreate.class) @RequestBody EmploymentRequestDto dto);


    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar datos de empleo"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado", content = @Content)
    })
    ResponseEntity<EmploymentResponseDto> update(
            @PathVariable UUID id,
            @Validated(OnUpdate.class) @RequestBody EmploymentRequestDto dto);


    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar un empleado por ID",
            description = "Devuelve un empleado específico si está activo."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado encontrado"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado", content = @Content)
    })
    ResponseEntity<EmploymentResponseDto> getById(@PathVariable UUID id);


    @GetMapping("/user/{email}")
    @Operation(
            summary = "Listar empleos de un usuario",
            description = "Devuelve todos los empleos relacionados a un usuario."
    )
    ResponseEntity<Page<EmploymentResponseDto>> getEmploymentsByUser(
            @PathVariable String email, @Parameter Pageable pageable);


    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un empleado",
            description = "Marca como eliminado al usuario asociado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Empleado eliminado"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado", content = @Content)
    })
    ResponseEntity<Void> delete(@PathVariable UUID id);


    @GetMapping("/actives")
    @Operation(
            summary = "Listar empleados activos",
            description = "Devuelve todos los empleados que no han sido eliminados."
    )
    ResponseEntity<Page<EmploymentResponseDto>> getActiveEmployees(Pageable pageable);


    @GetMapping("/deleted")
    @Operation(
            summary = "Listar empleados eliminados",
            description = "Devuelve todos los empleados cuyo usuario fue eliminado."
    )
    ResponseEntity<Page<EmploymentResponseDto>> getDeletedEmployees(Pageable pageable);

}
