package com.group_three.food_ordering.dto.request;

import com.group_three.food_ordering.enums.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PaymentRequestDto {

    @NotNull(message = "El método de pago es obligatorio")
    private PaymentMethod paymentMethod;

    @NotEmpty(message = "Debe seleccionar al menos una orden para asociar al pago")
    private List<UUID> orderIds;
}
