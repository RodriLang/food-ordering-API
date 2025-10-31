package com.group_three.food_ordering.notifications.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.notifications.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class NotificationResponseDto {

    private String title;

    private String mensaje;

    private boolean unread = true;

    private LocalDateTime creationDate = LocalDateTime.now();

    private NotificationType type;

    private String linkUrl;

}
