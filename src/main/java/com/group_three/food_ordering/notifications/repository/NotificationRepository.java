package com.group_three.food_ordering.notifications.repository;

import com.group_three.food_ordering.notifications.models.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findByPublicId(UUID uuid);

    List<Notification> findByTargetUser_EmailAndUnreadTrue(String userEmail);

    Page<Notification> findByTargetUser_EmailOrderByCreationDateDesc(String userEmail, Pageable pageable);

    Long countByTargetUser_EmailAndUnreadTrue(String userEmail);

}