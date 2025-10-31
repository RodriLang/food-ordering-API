package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.enums.EmploymentStatus;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.models.Employment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmploymentRepository extends JpaRepository<Employment, UUID>, JpaSpecificationExecutor<Employment> {
    Optional<Employment> findByPublicIdAndActiveAndDeletedFalse(UUID publicId, Boolean active);

    Optional<Employment> findByPublicIdAndFoodVenue_PublicIdAndActiveAndDeletedFalse(UUID publicId, UUID foodVenueId, Boolean active);

    Optional<Employment> findByInvitationToken(String invitationToken);

    Page<Employment> findByUser_PublicIdAndDeletedFalseAndDeletedFalse(UUID userPublicId, Pageable pageable);

    Page<Employment> getAllByActiveAndDeletedFalse(Pageable pageable, Boolean active);

    Page<Employment> getAllByActiveAndRoleAndDeletedFalse(Pageable pageable, Boolean active, RoleType role);

    Page<Employment> getAllByRoleAndDeletedFalse(Pageable pageable, RoleType role);

    List<Employment> findByUser_PublicIdAndDeletedFalseAndDeletedFalse(UUID userPublicId);

    List<Employment> findByUser_PublicIdAndRoleAndActiveTrueAndDeletedFalse(UUID userId, RoleType role);

    List<Employment> findByUser_EmailAndFoodVenue_PublicIdAndActiveAndDeletedFalse(String userEmail, UUID foodVenuePublicId, Boolean active);

    Page<Employment> getAllByActiveAndFoodVenue_PublicIdAndDeletedFalse(Pageable pageable, Boolean active, UUID foodVenuePublicId);

    Page<Employment> getAllByActiveAndRoleAndFoodVenue_PublicIdAndDeletedFalse(Pageable pageable, Boolean active, RoleType role, UUID foodVenuePublicId);

    List<Employment> findByUser_PublicIdAndFoodVenue_PublicIdAndDeletedFalseAndDeletedFalse(UUID userPublicId, UUID foodVenuePublicId);

    List<Employment> findByUser_PublicIdAndRoleAndActiveTrueAndFoodVenue_PublicIdAndDeletedFalse(UUID userPublicId, RoleType role, UUID foodVenuePublicId);

    List<Employment> findByStatusAndTokenExpirationBefore(EmploymentStatus status, Instant now);
}
