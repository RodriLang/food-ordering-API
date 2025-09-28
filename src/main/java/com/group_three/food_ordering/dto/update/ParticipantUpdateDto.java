package com.group_three.food_ordering.dto.update;

import jakarta.validation.Valid;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantUpdateDto {

    @Valid
    private UserUpdateDto user;
}
