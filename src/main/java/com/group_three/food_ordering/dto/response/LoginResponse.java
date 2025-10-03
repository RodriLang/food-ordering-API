package com.group_three.food_ordering.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LoginResponse {

    private AuthResponse authResponse;

    private List<RoleEmploymentResponseDto> employments;

}
