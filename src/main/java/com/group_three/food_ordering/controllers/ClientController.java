package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dtos.create.ClientCreateDto;
import com.group_three.food_ordering.dtos.response.ClientResponseDto;
import com.group_three.food_ordering.dtos.update.ClientPatchDto;
import com.group_three.food_ordering.dtos.update.ClientUpdateDto;
import com.group_three.food_ordering.services.interfaces.IClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.CLIENT_BASE)
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Gestión de clientes registrados e invitados.")
public class ClientController {

    private final IClientService clientService;


    @PostMapping
    @Operation(
            summary = "Crear un nuevo cliente",
            description = "Permite registrar un cliente. Puede tener un usuario completo (asignando automáticamente ROLE_CLIENT) o ser un cliente invitado (usuario null, nickname 'Invitado' y ROLE_GUEST).",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
            }
    )

    public ResponseEntity<ClientResponseDto> create(
            @Valid
            @org.springframework.web.bind.annotation.RequestBody
            ClientCreateDto dto) {
        ClientResponseDto createdClient = clientService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdClient);
    }


    @PreAuthorize("hasAnyRole('ADMIN','STAFF', 'SUPER_ADMIN','ROOT')")
    @GetMapping
    @Operation(
            summary = "Obtener todos los clientes",
            description = "Devuelve una lista de todos los clientes activos (no eliminados)."
    )
    public ResponseEntity<List<ClientResponseDto>> getAll() {
        return ResponseEntity.ok(clientService.getAll());
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN','ROOT')")
    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar cliente por ID",
            description = "Obtiene la información de un cliente por su ID, si no está eliminado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
                    @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
            }
    )
    public ResponseEntity<ClientResponseDto> getById(
            @PathVariable UUID id) {
        return ResponseEntity.ok(clientService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN','ROOT')")
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar cliente lógicamente",
            description = "Marca como eliminado al cliente asignando removedAt al User asociado."
    )
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN','ROOT','CLIENT')")
    @PutMapping("/{id}")
    @Operation(
            summary = "Reemplazar completamente un cliente",
            description = "Reemplaza los datos del usuario embebido en el cliente. No se puede modificar el nickname.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente actualizado correctamente"),
                    @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
            }
    )
    public ResponseEntity<ClientResponseDto> replace(
            @PathVariable UUID id,
            @Valid @org.springframework.web.bind.annotation.RequestBody ClientUpdateDto dto) {
        return ResponseEntity.ok(clientService.replace(id, dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN','ROOT','CLIENT')")
    @PatchMapping("/{id}")
    @Operation(
            summary = "Actualizar parcialmente un cliente",
            description = "Modifica parcialmente los datos del usuario embebido. No se puede modificar el nickname.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente modificado correctamente"),
                    @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
            }
    )
    public ResponseEntity<ClientResponseDto> partialUpdate(
            @PathVariable UUID id,
            @org.springframework.web.bind.annotation.RequestBody ClientPatchDto dto) {
        return ResponseEntity.ok(clientService.partialUpdate(id, dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN','ROOT')")
    @GetMapping("/all")
    @Operation(
            summary = "Listar todos los clientes",
            description = "Devuelve todos los clientes registrados, incluyendo los eliminados."
    )
    public ResponseEntity<List<ClientResponseDto>> getAllClients() {
        return ResponseEntity.ok(clientService.getAll());
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN','ROOT')")
    @GetMapping("/actives")
    @Operation(
            summary = "Listar clientes activos",
            description = "Devuelve todos los clientes que no han sido eliminados (removedAt es null en su usuario)."
    )
    public ResponseEntity<List<ClientResponseDto>> getActiveClients() {
        List<ClientResponseDto> actives = clientService.getAll().stream()
                .filter(c -> c.getUser() == null || c.getUser().getRemovedAt() == null)
                .toList();
        return ResponseEntity.ok(actives);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN','ROOT')")
    @GetMapping("/deleted")
    @Operation(
            summary = "Listar clientes eliminados",
            description = "Devuelve todos los clientes cuyo usuario fue eliminado (removedAt no es null)."
    )
    public ResponseEntity<List<ClientResponseDto>> getDeletedClients() {
        List<ClientResponseDto> deleted = clientService.getAll().stream()
                .filter(c -> c.getUser() != null && c.getUser().getRemovedAt() != null)
                .toList();
        return ResponseEntity.ok(deleted);
    }
}

