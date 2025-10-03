package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.enums.TableStatus;
import com.group_three.food_ordering.models.TableSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TableSessionRepository extends JpaRepository<TableSession, UUID> {

    List<TableSession> findByFoodVenueIdAndDeletedFalse(UUID foodVenueId);

    List<TableSession> findByFoodVenueIdAndTableNumberAndDeletedFalse(UUID foodVenueId, Integer tableNumber);

    List<TableSession> findByFoodVenueIdAndTableNumberAndEndTimeGreaterThanEqualAndStartTimeLessThanEqualAndDeletedFalse(
            UUID foodVenueId, Integer tableNumber, LocalDateTime start, LocalDateTime end);

    List<TableSession> findByFoodVenueIdAndEndTimeIsNullAndDeletedFalse(UUID foodVenueId);

    List<TableSession> findByFoodVenueIdAndSessionHostIdAndDeletedFalse(UUID foodVenueId, UUID sessionHost);

    @Query("SELECT ts FROM table_sessions ts " +
            "JOIN ts.participants p " +
            "WHERE p.id = :clientId " +
            "AND ts.endTime IS NOT NULL")
    List<TableSession> findPastSessionsByParticipantIdAndDeletedFalse(UUID foodVenueId, UUID clientId);

    Optional<TableSession> findTopByFoodVenueIdAndDeletedFalseAndTableIdOrderByStartTimeDesc(UUID foodVenueId, UUID tableId);

    Optional<TableSession> findTableSessionByTable_IdAndTableStatusAndDeletedFalse(UUID tableId, TableStatus status);

    @Query("SELECT ts FROM table_sessions ts " +
            "JOIN ts.participants p " +
            "WHERE p.user.email = :userEmail " +
            "AND ts.endTime IS NULL " +
            "AND ts.deleted = false")
    Optional<TableSession> findActiveSessionByUserEmailAndDeletedFalse(@Param("userEmail") String userEmail);
}
