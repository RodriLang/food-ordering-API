package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.dto.response.PageResponse;
import com.group_three.food_ordering.utils.OnCreate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping(ApiPaths.ADMIN_URI) // -> /api/v1/root/admins
@Tag(name = "Admins", description = "Gestión de usuarios Administradores por un usuario Root.")
public interface AdminEmploymentController {

    @PostMapping
    @Operation(summary = "Registrar un nuevo usuario Administrador")
    @ApiResponse(responseCode = "201", description = "Admin creado exitosamente")
    ResponseEntity<EmploymentResponseDto> registerAdmin(
            @Validated(OnCreate.class) @RequestBody EmploymentRequestDto dto);

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un admin por ID")
    ResponseEntity<EmploymentResponseDto> getById(@PathVariable UUID id);

    @GetMapping
    @Operation(summary = "Listar administradores", description = "Lista todos los admins del Food Venue actual, filtrados por estado.")
    ResponseEntity<PageResponse<EmploymentResponseDto>> getAllAdmins(
            @RequestParam(required = false) Boolean active,
            @Parameter(hidden = true) Pageable pageable);

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar (lógicamente) un usuario administrador")
    @ApiResponse(responseCode = "204", description = "Admin eliminado exitosamente")
    ResponseEntity<Void> deleteById(@PathVariable UUID id);
}