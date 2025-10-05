package com.group_three.food_ordering.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlatMenuResponseDto {

    private String foodVenueName;

    private String foodVenueImageUrl;

    private List<FlatCategoryMenuResponseDto> menu;

}
