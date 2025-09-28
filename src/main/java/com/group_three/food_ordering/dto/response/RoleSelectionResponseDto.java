package com.group_three.food_ordering.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Builder
@Data
@EqualsAndHashCode(callSuper = false)
public class RoleSelectionResponseDto implements LoginResponse {

    private String token;

    private List<RoleEmploymentResponseDto> employments;

}
