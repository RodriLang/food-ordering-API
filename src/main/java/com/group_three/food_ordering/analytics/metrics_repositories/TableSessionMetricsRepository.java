package com.group_three.food_ordering.analytics.metrics_repositories;

import com.group_three.food_ordering.analytics.metrics_dto.AverageSessionDurationProjection;
import com.group_three.food_ordering.models.TableSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface TableSessionMetricsRepository extends JpaRepository<TableSession, Long> {

    @Query(value = """
  SELECT
    LOWER(CONCAT(
      SUBSTR(HEX(v.public_id),1,8),'-',SUBSTR(HEX(v.public_id),9,4),'-',
      SUBSTR(HEX(v.public_id),13,4),'-',SUBSTR(HEX(v.public_id),17,4),'-',
      SUBSTR(HEX(v.public_id),21)
    )) AS venueId,
    v.name AS venueName,
    CAST(AVG(TIMESTAMPDIFF(MINUTE, ts.start_time, ts.end_time)) AS DOUBLE) AS averageSessionDurationMinutes
  FROM table_sessions ts
  JOIN dining_tables dt ON ts.dining_table_id = dt.id
  JOIN food_venues v    ON dt.food_venue_id   = v.id
  WHERE ts.start_time BETWEEN :from AND :to
    AND ts.end_time IS NOT NULL
  GROUP BY v.public_id, v.name
""", nativeQuery = true)
    List<AverageSessionDurationProjection> findAverageSessionDurationByVenue(Instant from, Instant to);


    @Query(value = """
  SELECT COALESCE(
           CAST(AVG(TIMESTAMPDIFF(MINUTE, ts.start_time, ts.end_time)) AS DOUBLE),
           0
         )
  FROM table_sessions ts
  JOIN dining_tables dt ON ts.dining_table_id = dt.id
  WHERE dt.food_venue_id = :venueId
    AND ts.end_time IS NOT NULL
    AND ts.start_time BETWEEN :from AND :to
""", nativeQuery = true)
    Double findAverageSessionDurationByVenueId(@Param("venueId") UUID venueId,
                                               @Param("from") Instant from,
                                               @Param("to") Instant to);


}
