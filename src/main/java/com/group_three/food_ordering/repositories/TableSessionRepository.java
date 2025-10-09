package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.enums.DiningTableStatus;
import com.group_three.food_ordering.models.Participant;
import com.group_three.food_ordering.models.TableSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TableSessionRepository extends JpaRepository<TableSession, Long> {

    Optional<TableSession> findByPublicId(UUID publicId);

    Optional<TableSession> findByParticipantsContains(List<Participant> participants);

    Page<TableSession> findByFoodVenuePublicId(UUID foodVenueId, Pageable pageable);

    Page<TableSession> findByFoodVenuePublicIdAndDiningTableNumber(UUID foodVenueId, Integer tableNumber, Pageable pageable);

    Page<TableSession> findByFoodVenuePublicIdAndDiningTableNumberAndEndTimeGreaterThanEqualAndStartTimeLessThanEqual(
            UUID foodVenueId, Integer tableNumber, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<TableSession> findByFoodVenuePublicIdAndEndTimeIsNull(UUID foodVenueId, Pageable pageable);

    Page<TableSession> findByFoodVenuePublicIdAndSessionHostPublicId(UUID foodVenueId, UUID sessionHost, Pageable pageable);

    @Query("SELECT ts FROM TableSession ts " +
            "JOIN ts.participants p " +
            "WHERE p.id = :clientId " +
            "AND ts.endTime IS NOT NULL")
    Page<TableSession> findPastSessionsByParticipantIdAndDeletedFalse(UUID foodVenueId, UUID clientId, Pageable pageable);

    Optional<TableSession> findTopByFoodVenuePublicIdAndDiningTablePublicIdOrderByStartTimeDesc(UUID foodVenueId, UUID tableId);

    Optional<TableSession> findTableSessionByDiningTable_PublicIdAndDiningTableStatus(UUID tableId, DiningTableStatus status);

    @Query("SELECT ts FROM TableSession ts " +
            "JOIN ts.participants p " +
            "WHERE p.user.email = :userEmail " +
            "AND ts.endTime IS NULL ")
    Optional<TableSession> findActiveSessionByUserEmailAndDeletedFalse(@Param("userEmail") String userEmail);
}
