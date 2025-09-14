package com.group_three.food_ordering.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableSessionCreateDto {

    @NotNull(message = "Table ID is required")
    private UUID tableId;

}

