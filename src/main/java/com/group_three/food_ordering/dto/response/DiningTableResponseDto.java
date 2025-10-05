package com.group_three.food_ordering.dto.response;

import com.group_three.food_ordering.enums.DiningTableStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiningTableResponseDto {

    private UUID publicId;

    private Integer number;

    private Integer capacity;

    private DiningTableStatus status;

}

