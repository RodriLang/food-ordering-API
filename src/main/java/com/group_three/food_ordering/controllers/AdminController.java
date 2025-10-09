package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.dto.response.PageResponse;
import com.group_three.food_ordering.utils.OnCreate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping(ApiPaths.ADMIN_URI)
@Tag(name = "Admins", description = "Gestión de usuarios administradores con acceso root")
public interface AdminController {

    @PostMapping
    @Operation(
            summary = "Registrar un nuevo usuario administrador",
            description = "Crea un admin vinculando a un User existente con un Lugar de comida.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Admin creado exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Empleo o Lugar de comida no encontrados"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
            }
    )
    ResponseEntity<EmploymentResponseDto> registerAdmin(
            @Validated(OnCreate.class) @RequestBody EmploymentRequestDto dto);

    @GetMapping("/id/{id}")
    @Operation(
            summary = "Obtener un admin por ID",
            description = "Devuelve un empleo administrador por su ID si está activo.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Empleo encontrado"),
                    @ApiResponse(responseCode = "404", description = "Empleo no encontrado", content = @Content)
            }
    )
    ResponseEntity<EmploymentResponseDto> getById(@PathVariable UUID id);

    @GetMapping("/email/{email}")
    @Operation(
            summary = "Obtener admin por email",
            description = "Devuelve un administrador por su email si está activo.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Empleo encontrado"),
                    @ApiResponse(responseCode = "404", description = "Empleo no encontrado", content = @Content)
            }
    )
    ResponseEntity<EmploymentResponseDto> getByEmail(@PathVariable String email);

    @GetMapping("/all")
    @Operation(
            summary = "Listar todos los admin",
            description = "Devuelve todos los administradores registrados del lugar de comida actual, incluyendo los inactivos."
    )
    ResponseEntity<PageResponse<EmploymentResponseDto>> getAll(@Parameter(hidden = true) Pageable pageable);

    @GetMapping("/actives")
    @Operation(
            summary = "Listar administradores activos",
            description = "Devuelve todos los admin del lugar de comida actual que están activos."
    )
    ResponseEntity<PageResponse<EmploymentResponseDto>> getActives(@Parameter(hidden = true) Pageable pageable);

    @GetMapping("/removed")
    @Operation(
            summary = "Listar admin eliminados",
            description = "Devuelve todos los administradores del lugar de comida actual que han sido marcados como inactivos."
    )
    ResponseEntity<PageResponse<EmploymentResponseDto>> getInactives(@Parameter(hidden = true) Pageable pageable);


    @PatchMapping("/{id}")
    @Operation(
            summary = "Actualizar parcialmente un administrados",
            description = "Modifica uno o varios campos del admin.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Admin actualizado parcialmente"),
                    @ApiResponse(responseCode = "404", description = "Admin no encontrado", content = @Content)
            }
    )
    ResponseEntity<EmploymentResponseDto> update(
            @PathVariable UUID id,
            @Validated(OnCreate.class) @RequestBody EmploymentRequestDto dto);


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Eliminar lógicamente un usuario administrador",
            description = "Marca el admin como eliminado estableciendo su campo active false."
    )
    ResponseEntity<Void> deleteById(@PathVariable UUID id);

}
