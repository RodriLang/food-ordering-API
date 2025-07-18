package com.group_three.food_ordering.dtos.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagResponseDto {

    private Long id;
    private String label;
}
