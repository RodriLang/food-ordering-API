package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPublicId(UUID publicId);

    @Query("SELECT u.publicId FROM User u WHERE u.email like :email AND u.deleted = false")
    Optional<UUID> findPublicIdByEmailAndDeletedFalse(String email);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.deleted = true")
    Page<User> findAllDeleted(Pageable pageable);

}
