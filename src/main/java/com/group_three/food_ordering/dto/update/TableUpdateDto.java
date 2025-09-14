package com.group_three.food_ordering.dto.update;

import com.group_three.food_ordering.enums.TableStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableUpdateDto {

    private UUID id;
    private int number;
    private int capacity;
    private TableStatus status;
}
