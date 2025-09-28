package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.enums.TableStatus;
import com.group_three.food_ordering.models.TableSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TableSessionRepository extends JpaRepository<TableSession, UUID> {

    List<TableSession> findByFoodVenueId(UUID foodVenueId);
    List<TableSession> findByFoodVenueIdAndTableNumber(UUID foodVenueId, Integer tableNumber);
    List<TableSession> findByFoodVenueIdAndTableNumberAndEndTimeGreaterThanEqualAndStartTimeLessThanEqual(
            UUID foodVenueId, Integer tableNumber, LocalDateTime start, LocalDateTime end);
    List<TableSession> findByFoodVenueIdAndEndTimeIsNull(UUID foodVenueId);
    List<TableSession> findByFoodVenueIdAndSessionHostId(UUID foodVenueId, UUID sessionHost);

    @Query("SELECT ts " +
            "FROM table_sessions ts " +
            "JOIN ts.participants p " +
            "WHERE p.id = :clientId " +
            "AND ts.endTime " +
            "IS NOT NULL")
    List<TableSession> findPastSessionsByParticipantId(UUID foodVenueId, UUID clientId);
    Optional<TableSession> findTopByFoodVenueIdAndTableIdOrderByStartTimeDesc(UUID foodVenueId, UUID tableId);
Optional<TableSession> findTableSessionByTable_IdAndTableStatus(UUID tableId, TableStatus status);
}
