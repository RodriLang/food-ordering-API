package com.group_three.food_ordering.notifications.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.group_three.food_ordering.notifications.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class NotificationResponseDto {

    private UUID publicId;

    private String title;

    private String mensaje;

    private boolean unread = true;

    private LocalDateTime creationDate = LocalDateTime.now();

    private NotificationType type;

    private String linkUrl;

}
