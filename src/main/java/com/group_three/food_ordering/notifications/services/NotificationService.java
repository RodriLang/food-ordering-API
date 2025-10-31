package com.group_three.food_ordering.notifications.services;

import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.NotificationMapper;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.notifications.dto.NotificationResponseDto;
import com.group_three.food_ordering.notifications.dto.UnreadCountDto;
import com.group_three.food_ordering.notifications.enums.NotificationType;
import com.group_three.food_ordering.notifications.models.Notification;
import com.group_three.food_ordering.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final long USER_TIMEOUT = 3_600_000L; // 1 hora
    private final Map<String, SseEmitter> userEmitters = new ConcurrentHashMap<>();

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public SseEmitter subscribeUser(String userEmail) {
        SseEmitter emitter = new SseEmitter(USER_TIMEOUT);
        userEmitters.put(userEmail, emitter);

        emitter.onCompletion(() -> userEmitters.remove(userEmail));
        emitter.onTimeout(() -> userEmitters.remove(userEmail));
        emitter.onError(e -> {
            log.warn("Error en SseEmitter para usuario {}: {}", userEmail, e.getMessage());
            userEmitters.remove(userEmail);
        });

        log.info("Usuario {} suscrito a notificaciones personales", userEmail);
        return emitter;
    }

    @Transactional
    public void createPersistentNotification(User user, String title, String mensaje, NotificationType type, String link) {

        Notification notification = Notification.builder()
                .targetUser(user)
                .title(title)
                .mensaje(mensaje)
                .type(type)
                .linkUrl(link)
                .unread(true)
                .deleted(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notificación persistente creada para el usuario {}", user.getEmail());

        SseEmitter emitter = userEmitters.get(user.getEmail());
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("new_notification")
                        .data(notificationMapper.toDto(savedNotification)));
            } catch (Exception e) {
                log.warn("Fallo al enviar notificación SSE al usuario {}: {}", user.getEmail(), e.getMessage());
            }
        }
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponseDto> getNotificationsForUser(String userEmail, Pageable pageable) {
        Page<Notification> notificationPage = notificationRepository
                .findByTargetUser_EmailOrderByCreationDateDesc(userEmail, pageable);
        return notificationPage.map(notificationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public UnreadCountDto getUnreadCount(String userEmail) {
        Long count = notificationRepository.countByTargetUser_EmailAndUnreadTrue(userEmail);
        return new UnreadCountDto(count);
    }

    @Transactional
    public NotificationResponseDto markAsRead(UUID notificationId, String userEmail) {
        Notification notification = notificationRepository.findByPublicId(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification", notificationId.toString()));

        // Verifica que el usuario que hace el request sea el dueño de la notificación.
        if (!notification.getTargetUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("No tienes permiso para modificar esta notificación");
        }

        notification.setUnread(false);
        notification.setReadDateTime(LocalDateTime.now());
        Notification updatedNotification = notificationRepository.save(notification);
        return notificationMapper.toDto(updatedNotification);
    }

    @Transactional
    public void markAllAsRead(String userEmail) {
        List<Notification> unreadNotifications = notificationRepository
                .findByTargetUser_EmailAndUnreadTrue(userEmail);

        if (unreadNotifications.isEmpty()) {
            return;
        }

        for (Notification notification : unreadNotifications) {
            notification.setUnread(false);
            notification.setReadDateTime(LocalDateTime.now());
        }

        notificationRepository.saveAll(unreadNotifications);
        log.info("Marcadas {} notificaciones como leídas para el usuario {}", unreadNotifications.size(), userEmail);
    }
}