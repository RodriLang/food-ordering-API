package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, UUID> {

    Optional<Participant> findByUser_EmailAndDeletedFalse(String email);

    boolean existsByUser_EmailAndDeletedFalse(String email);

    boolean existsByNicknameAndDeletedFalse(String nickname);

    List<Participant> findAllByUser_RemovedAtIsNullAndDeletedFalse();

    Optional<Participant> findByIdAndUser_RemovedAtIsNullAndDeletedFalse(UUID id);
}

