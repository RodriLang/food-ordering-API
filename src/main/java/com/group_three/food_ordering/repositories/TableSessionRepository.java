package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.enums.DiningTableStatus;
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

    List<TableSession> findByFoodVenuePublicId(UUID foodVenueId);

    List<TableSession> findByFoodVenuePublicIdAndDiningTableNumber(UUID foodVenueId, Integer tableNumber);

    List<TableSession> findByFoodVenuePublicIdAndDiningTableNumberAndEndTimeGreaterThanEqualAndStartTimeLessThanEqual(
            UUID foodVenueId, Integer tableNumber, LocalDateTime start, LocalDateTime end);

    List<TableSession> findByFoodVenuePublicIdAndEndTimeIsNull(UUID foodVenueId);

    List<TableSession> findByFoodVenuePublicIdAndSessionHostPublicId(UUID foodVenueId, UUID sessionHost);

    @Query("SELECT ts FROM TableSession ts " +
            "JOIN ts.participants p " +
            "WHERE p.id = :clientId " +
            "AND ts.endTime IS NOT NULL")
    List<TableSession> findPastSessionsByParticipantIdAndDeletedFalse(UUID foodVenueId, UUID clientId);

    Optional<TableSession> findTopByFoodVenuePublicIdAndDiningTablePublicIdOrderByStartTimeDesc(UUID foodVenueId, UUID tableId);

    Optional<TableSession> findTableSessionByDiningTable_PublicIdAndDiningTableStatus(UUID tableId, DiningTableStatus status);

    @Query("SELECT ts FROM TableSession ts " +
            "JOIN ts.participants p " +
            "WHERE p.user.email = :userEmail " +
            "AND ts.endTime IS NULL ")
    Optional<TableSession> findActiveSessionByUserEmailAndDeletedFalse(@Param("userEmail") String userEmail);
}
