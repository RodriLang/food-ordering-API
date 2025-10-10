package com.group_three.food_ordering.analytics.metrics_services.impl;

import com.group_three.food_ordering.analytics.metrics_dto.*;
import com.group_three.food_ordering.analytics.metrics_repositories.OrderMetricsRepository;
import com.group_three.food_ordering.analytics.metrics_repositories.PaymentMetricsRepository;
import com.group_three.food_ordering.analytics.metrics_repositories.TableSessionMetricsRepository;
import com.group_three.food_ordering.analytics.metrics_services.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MetricsServiceImpl implements MetricsService {

    private final OrderMetricsRepository orderMetricsRepository;
    private final TableSessionMetricsRepository tableSessionMetricsRepository;
    private final PaymentMetricsRepository paymentMetricsRepository;

    // ---- MÉTRICAS GENERALES ----

    @Override
    public GeneralMetricsResponseDto getGeneralOverview(LocalDateTime from, LocalDateTime to) {
        long totalOrders = orderMetricsRepository.countOrdersBetween(from, to);
        long totalVenues = orderMetricsRepository.countDistinctVenuesBetween(from, to);
        BigDecimal totalRevenue = orderMetricsRepository.getRevenueGroupedByVenue(from, to).stream()
                .map(RevenueByVenueDto::getTotalRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double averageTicket = orderMetricsRepository.getRevenueGroupedByVenue(from, to).stream()
                .mapToDouble(RevenueByVenueDto::getAverageTicket)
                .average()
                .orElse(0.0);

        double averageSessionMinutes = tableSessionMetricsRepository.findAverageSessionDurationByVenue(from, to).stream()
                .mapToDouble(AverageSessionDurationDto::getAverageSessionDurationMinutes)
                .average()
                .orElse(0.0);

        return new GeneralMetricsResponseDto(
                totalOrders,
                totalVenues,
                totalRevenue,
                averageTicket,
                averageSessionMinutes
        );
    }

    @Override
    public List<OrdersByVenueDto> getOrdersByVenue(LocalDateTime from, LocalDateTime to) {
        return orderMetricsRepository.getOrdersGroupedByVenue(from, to);
    }

    @Override
    public List<RevenueByVenueDto> getRevenueByVenue(LocalDateTime from, LocalDateTime to) {
        return orderMetricsRepository.getRevenueGroupedByVenue(from, to);
    }

    @Override
    public List<RevenueByVenueDto> getTopVenuesByRevenue(LocalDateTime from, LocalDateTime to, int limit) {
        return orderMetricsRepository.getRevenueGroupedByVenue(from, to)
                .stream()
                .sorted((a, b) -> b.getTotalRevenue().compareTo(a.getTotalRevenue()))
                .limit(limit)
                .toList();
    }

    // ---- MÉTRICAS POR LOCAL ----

    @Override
    public VenueMetricsResponseDto getVenueOverview(UUID venueId, LocalDateTime from, LocalDateTime to) {
        long totalOrders = orderMetricsRepository.countByVenueAndDateBetween(venueId, from, to);
        BigDecimal totalRevenue = BigDecimal.valueOf(
                orderMetricsRepository.sumTotalRevenueByVenue(venueId, from, to)
        );
        double averageTicket = orderMetricsRepository.calculateAverageTicketByVenue(venueId, from, to);
        double averageSessionDuration = tableSessionMetricsRepository.findAverageSessionDurationByVenueId(venueId, from, to);
        double averageSpendingPerTable = paymentMetricsRepository.findAverageSpending(venueId, from, to);
        double cancellationRate = orderMetricsRepository.calculateCancellationRate(venueId, from, to);

        String venueName = orderMetricsRepository.findVenueNameById(venueId);

        return new VenueMetricsResponseDto(
                venueId,
                venueName,
                totalOrders,
                totalRevenue,
                averageTicket,
                averageSessionDuration,
                averageSpendingPerTable,
                cancellationRate
        );
    }

    @Override
    public List<TemporalSalesDto> getSalesEvolution(UUID venueId, LocalDateTime from, LocalDateTime to, String groupBy) {
        // Implementación pendiente según "day", "week", "month"
        return List.of();
    }

    @Override
    public List<ProductSalesDto> getTopProducts(UUID venueId, LocalDateTime from, LocalDateTime to, int limit) {
        // Implementación pendiente: agregar query en repository
        return List.of();
    }

    @Override
    public List<EmployeePerformanceDto> getEmployeePerformance(UUID venueId, LocalDateTime from, LocalDateTime to) {
        // Implementación pendiente: agregar query en repository
        return List.of();
    }
}
