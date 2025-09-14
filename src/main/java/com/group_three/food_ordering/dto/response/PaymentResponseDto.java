package com.group_three.food_ordering.dto.response;

import com.group_three.food_ordering.enums.PaymentMethod;
import com.group_three.food_ordering.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDto {

    private UUID id;
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
}
