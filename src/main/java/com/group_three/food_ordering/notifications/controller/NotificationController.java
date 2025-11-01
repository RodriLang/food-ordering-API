package com.group_three.food_ordering.notifications.controller;

import com.group_three.food_ordering.configs.security.CustomUserPrincipal;
import com.group_three.food_ordering.notifications.dto.NotificationResponseDto;
import com.group_three.food_ordering.notifications.dto.UnreadCountDto;
import com.group_three.food_ordering.notifications.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/subscribe")
    public SseEmitter subscribeToUserNotifications(
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        return notificationService.subscribeUser(principal.getEmail()); // Asumo que tu principal tiene el email
    }

    @GetMapping
    public ResponseEntity<Page<NotificationResponseDto>> getMyNotifications(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            Pageable pageable) {

        Page<NotificationResponseDto> notifications = notificationService.getNotificationsForUser(principal.getEmail(), pageable);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<UnreadCountDto> getMyUnreadCount(
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        UnreadCountDto countDto = notificationService.getUnreadCount(principal.getEmail());
        return ResponseEntity.ok(countDto);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponseDto> markNotificationAsRead(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        NotificationResponseDto updatedNotification = notificationService.markAsRead(id, principal.getEmail());
        return ResponseEntity.ok(updatedNotification);
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead(
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        notificationService.markAllAsRead(principal.getEmail());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}