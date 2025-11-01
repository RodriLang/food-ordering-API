package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.notifications.dto.NotificationResponseDto;
import com.group_three.food_ordering.notifications.models.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponseDto toDto(Notification notification);

    Notification toEntity(NotificationResponseDto notificationResponseDto);

}
