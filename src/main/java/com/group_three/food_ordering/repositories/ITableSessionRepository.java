package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.TableSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ITableSessionRepository extends JpaRepository<TableSession, UUID> {

    List<TableSession> findByFoodVenueIdAndTableIdAndStartTimeAfter(UUID foodVenueId, UUID tableId, LocalDateTime since);
    List<TableSession> findByFoodVenueIdAndEndTimeIsNull(UUID foodVenueId);
    List<TableSession> findByFoodVenueIdAndHostClientId(UUID foodVenueId, UUID clientId);
    Optional<TableSession> findByFoodVenueIdAndIdAndEndTimeIsNull(UUID foodVenueId, UUID sessionId);
    Optional<TableSession> findFirstByFoodVenueIdAndTableIdOrderByStartTimeDesc(UUID foodVenueId, UUID tableId);

    @Query("SELECT ts FROM table_sessions ts JOIN ts.participants p WHERE p.id = :clientId AND ts.endTime IS NOT NULL")
    List<TableSession> findPastSessionsByParticipantId(UUID clientId);
}
