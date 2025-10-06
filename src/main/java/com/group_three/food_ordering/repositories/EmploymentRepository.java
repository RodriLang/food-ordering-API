package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.models.Employment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmploymentRepository extends JpaRepository<Employment, Long> {

    Optional<Employment> findByPublicIdAndActive(UUID publicId, Boolean active);

    Optional<Employment> findByPublicIdAndFoodVenue_PublicIdAndActive(UUID publicId, UUID foodVenueId, Boolean active);

    Page<Employment> findByUser_PublicId(UUID userPublicId, Pageable pageable);

    Page<Employment> getAllByActive(Pageable pageable, Boolean active);

    Page<Employment> getAllByActiveAndRole(Pageable pageable, Boolean active, RoleType role);

    List<Employment> findByUser_PublicId(UUID userPublicId);

    List<Employment> findByUser_PublicIdAndRoleAndActiveTrue(UUID userId, RoleType role);

    Page<Employment> findByUser_PublicIdAndFoodVenue_PublicId(UUID userPublicId, Pageable pageable, UUID foodVenuePublicId);

    Page<Employment> getAllByActiveAndFoodVenue_PublicId(Pageable pageable, Boolean active, UUID foodVenuePublicId);

    Page<Employment> getAllByActiveAndRoleAndFoodVenue_PublicId(Pageable pageable, Boolean active, RoleType role, UUID foodVenuePublicId);

    List<Employment> findByUser_PublicIdAndFoodVenue_PublicId(UUID userPublicId, UUID foodVenuePublicId);

    List<Employment> findByUser_PublicIdAndRoleAndActiveTrueAndFoodVenue_PublicId(UUID userPublicId, RoleType role, UUID foodVenuePublicId);

}
