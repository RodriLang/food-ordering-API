package com.group_three.food_ordering.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeaturedProductResponseDto {

    private UUID productId;

    private Instant featuredFrom = Instant.now();

    private Instant featuredUntil;

    private Integer priority = 0;

}

