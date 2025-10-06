package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Optional<Participant> findByUser_Email(String email);

    Optional<Participant> findByPublicId(UUID publicId);

    boolean existsByUser_Email(String email);

    boolean existsByNickname(String nickname);

}

