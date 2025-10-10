package com.group_three.food_ordering.analytics.metrics_repositories;

import com.group_three.food_ordering.analytics.metrics_dto.AverageSessionDurationDto;
import com.group_three.food_ordering.models.TableSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TableSessionMetricsRepository extends JpaRepository<TableSession, Long> {

    @Query(value = """
        SELECT
            v.public_id AS venueId,
            v.name AS venueName,
            AVG(TIMESTAMPDIFF(MINUTE, ts.start_time, ts.end_time)) AS averageSessionDurationMinutes
        FROM table_sessions ts
        JOIN dining_tables dt ON ts.dining_table_id = dt.id
        JOIN food_venues v ON dt.food_venue_id = v.id
        WHERE ts.start_time BETWEEN :from AND :to
          AND ts.end_time IS NOT NULL
        GROUP BY v.public_id, v.name
    """, nativeQuery = true)
    List<AverageSessionDurationDto> findAverageSessionDurationByVenue(LocalDateTime from, LocalDateTime to);

    @Query(value = """
        SELECT AVG(TIMESTAMPDIFF(MINUTE, ts.start_time, ts.end_time))
        FROM table_sessions ts
        JOIN dining_tables dt ON ts.dining_table_id = dt.id
        JOIN food_venues v ON dt.food_venue_id = v.id
        WHERE v.public_id = :venueId
          AND ts.start_time BETWEEN :from AND :to
          AND ts.end_time IS NOT NULL
    """, nativeQuery = true)
    Double findAverageSessionDurationByVenueId(UUID venueId, LocalDateTime from, LocalDateTime to);
}
