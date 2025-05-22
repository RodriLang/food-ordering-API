package com.group_three.food_ordering.dtos.update;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientUpdateDto {

    @Valid
    private UserUpdateDto user;

    @NotBlank(message = "Nickname is required")
    private String nickname;
}
