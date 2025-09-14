package com.group_three.food_ordering.dto.update;

import jakarta.validation.Valid;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientPatchDto {

    @Valid
    private UserPatchDto user;
}
