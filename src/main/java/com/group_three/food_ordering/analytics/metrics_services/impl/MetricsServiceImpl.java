package com.group_three.food_ordering.analytics.metrics_services.impl;

import com.group_three.food_ordering.analytics.enums.TimeBucket;
import com.group_three.food_ordering.analytics.metrics_dto.*;
import com.group_three.food_ordering.analytics.metrics_repositories.OrderMetricsRepository;
import com.group_three.food_ordering.analytics.metrics_repositories.PaymentMetricsRepository;
import com.group_three.food_ordering.analytics.metrics_repositories.ProductsMetricsRepository;
import com.group_three.food_ordering.analytics.metrics_repositories.TableSessionMetricsRepository;
import com.group_three.food_ordering.analytics.metrics_services.MetricsService;
import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MetricsServiceImpl implements MetricsService {

    private final OrderMetricsRepository orderMetricsRepository;
    private final TableSessionMetricsRepository tableSessionMetricsRepository;
    private final PaymentMetricsRepository paymentMetricsRepository;
    private final ProductsMetricsRepository productsMetricsRepository;
    private final TenantContext tenantContext;

    private static TemporalSalesDto apply(Map<String, Object> r) {
        String bucket = String.valueOf(r.get("bucket"));
        Number ordersNum = (Number) r.get("ordersCount");           // COUNT(*) viene como Long/BigInteger
        Number revNum = (Number) r.get("revenue");               // SUM(...) suele venir BigDecimal

        long ordersCount = ordersNum != null ? ordersNum.longValue() : 0L;
        BigDecimal revenue;
        if ((revNum instanceof BigDecimal bd)) {
            revenue = bd;
        } else {
            if (revNum != null) revenue = BigDecimal.valueOf(revNum.doubleValue());
            else revenue = BigDecimal.ZERO;
        }

        return new TemporalSalesDto(bucket, ordersCount, revenue);
    }

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

        var perVenue = tableSessionMetricsRepository.findAverageSessionDurationByVenue(from, to);

        double averageSessionMinutes = perVenue.stream()
                .map(AverageSessionDurationProjection::getAverageSessionDurationMinutes) // -> Double
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
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
    public VenueMetricsResponseDto getVenueOverview(LocalDateTime from, LocalDateTime to) {
        UUID venueId = tenantContext.getCurrentFoodVenueId();
        long totalOrders = orderMetricsRepository.countByVenueAndDateBetween(venueId, from, to);
        BigDecimal totalRevenue = BigDecimal.valueOf(
                orderMetricsRepository.sumTotalRevenueByVenue(venueId, from, to)
        );
        double averageTicket = orderMetricsRepository.calculateAverageTicketByVenue(venueId, from, to);
        double averageSessionDuration = tableSessionMetricsRepository.findAverageSessionDurationByVenueId(venueId, from, to);
        double averageSpendingPerTable = paymentMetricsRepository.findAverageSpending(venueId, from, to);
        double cancellationRate = orderMetricsRepository.calculateCancellationRate(venueId, from, to);

        String venueName = orderMetricsRepository.findVenueNameById(venueId).getFirst();

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
    public List<TemporalSalesDto> getSalesEvolution(
            LocalDateTime from,
            LocalDateTime to,
            TimeBucket timeBucket,
            List<OrderStatus> statuses
    ) {
        // 1) Tenant
        UUID venueId = tenantContext != null ? tenantContext.getCurrentFoodVenueId() : null;

        // 2) Convertir enums a String si la query es nativa y usa IN (:statuses)
        List<String> statusStrings = statuses != null
                ? statuses.stream().map(Enum::name).toList()
                : List.of("PAID","COMPLETED"); // fallback

        // 3) Ejecutar query según bucket
        List<Map<String, Object>> rows = switch (timeBucket) {
            case DAY   -> orderMetricsRepository.salesByDay(from, to, statusStrings, venueId);
            case WEEK  -> orderMetricsRepository.salesByWeek(from, to, statusStrings, venueId);
            case MONTH -> orderMetricsRepository.salesByMonth(from, to, statusStrings, venueId);
        };

        // 4) Mapear defensivamente
        return rows.stream().map(MetricsServiceImpl::apply).toList();
    }



    @Override
    public List<ProductSalesDto> getTopProducts(LocalDateTime from, LocalDateTime to, int limit) {
        UUID venueId = tenantContext.getCurrentFoodVenueId();
        var statuses = List.of("PAID","COMPLETED");
        var rows = productsMetricsRepository.topProducts(from, to, statuses, limit, venueId);
        return rows.stream().map(r -> new ProductSalesDto(
                UUID.fromString((String) r.get("productId")),
                (String) r.get("productName"),
                ((Number) r.get("unitsSold")).longValue(),
                new BigDecimal(String.valueOf(r.get("revenue")))
        )).toList();
    }
}
