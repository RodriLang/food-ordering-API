package com.group_three.food_ordering.analytics.metrics_controllers;

import com.group_three.food_ordering.analytics.metrics_dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
@RequestMapping("/api/v1/metrics")
@Tag(name = "Métricas de análisis para administración", description = "Endpoints para métricas del local propio")
public interface MetricsAdminController {

    // ---- MÉTRICAS POR LOCAL (ADMIN) ----

    @GetMapping("/venue/{venueId}/overview")
    @Operation(summary = "Resumen por local", description = "Obtiene métricas específicas de un local entre fechas")
    ResponseEntity<VenueMetricsResponseDto> getVenueOverview(
            @Parameter(description = "ID del local") @PathVariable UUID venueId,
            @Parameter(description = "Fecha inicial", example = "2025-01-01T00:00:00") @RequestParam LocalDateTime from,
            @Parameter(description = "Fecha final", example = "2025-01-31T23:59:59") @RequestParam LocalDateTime to
    );

    @GetMapping("/venue/{venueId}/sales")
    @Operation(summary = "Evolución de ventas por local", description = "Obtiene la evolución de ventas por día, semana o mes")
    ResponseEntity<List<TemporalSalesDto>> getSalesEvolution(
            @Parameter(description = "ID del local") @PathVariable UUID venueId,
            @Parameter(description = "Fecha inicial", example = "2025-01-01T00:00:00") @RequestParam LocalDateTime from,
            @Parameter(description = "Fecha final", example = "2025-01-31T23:59:59") @RequestParam LocalDateTime to,
            @Parameter(description = "Agrupación de fechas: day, week, month", example = "day") @RequestParam(defaultValue = "day") String groupBy
    );

    @GetMapping("/venue/{venueId}/top-products")
    @Operation(summary = "Top productos por local", description = "Obtiene los productos más vendidos de un local")
    ResponseEntity<List<ProductSalesDto>> getTopProducts(
            @Parameter(description = "ID del local") @PathVariable UUID venueId,
            @Parameter(description = "Fecha inicial", example = "2025-01-01T00:00:00") @RequestParam LocalDateTime from,
            @Parameter(description = "Fecha final", example = "2025-01-31T23:59:59") @RequestParam LocalDateTime to,
            @Parameter(description = "Cantidad máxima de productos a devolver", example = "5") @RequestParam(defaultValue = "5") int limit
    );

    @GetMapping("/venue/{venueId}/employees")
    @Operation(summary = "Rendimiento de empleados por local", description = "Obtiene métricas de desempeño de los empleados de un local")
    ResponseEntity<List<EmployeePerformanceDto>> getEmployeePerformance(
            @Parameter(description = "ID del local") @PathVariable UUID venueId,
            @Parameter(description = "Fecha inicial", example = "2025-01-01T00:00:00") @RequestParam LocalDateTime from,
            @Parameter(description = "Fecha final", example = "2025-01-31T23:59:59") @RequestParam LocalDateTime to
    );
}
