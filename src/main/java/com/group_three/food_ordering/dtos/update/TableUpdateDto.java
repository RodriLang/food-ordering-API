package com.group_three.food_ordering.dtos.update;

import com.group_three.food_ordering.enums.TableStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableUpdateDto {

    private Long id;
    private int number;
    private int capacity;
    private TableStatus status;
}
