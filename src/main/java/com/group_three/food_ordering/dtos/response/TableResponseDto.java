package com.group_three.food_ordering.dtos.response;

import com.group_three.food_ordering.enums.TableStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableResponseDto {

    private Long id;
    private Integer number;
    private Integer capacity;
    private TableStatus status;
}

