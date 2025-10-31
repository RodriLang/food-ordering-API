package com.group_three.food_ordering.notifications.repository;

import com.group_three.food_ordering.notifications.models.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByTargetUser_EmailAndUnreadTrue(String userEmail);

    Page<Notification> findByTargetUser_EmailOrderByCreationDateDesc(String userEmail, Pageable pageable);

    Long countByTargetUser_EmailAndUnreadTrue(String userEmail);

}