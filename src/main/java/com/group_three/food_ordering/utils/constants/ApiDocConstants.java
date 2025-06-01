package com.group_three.food_ordering.utils.constants;

public class ApiDocConstants {
    public static final String PAYMENT_STATE_IRREVERSIBLE =
            "El cambio a alguno de estos estados será permanente. " +
                    "Por ejemplo: La lógica de negocio no permite que un pago cancelado " +
                    "vuelva a un estado pendiente o que un pago completado pese a un estado cancelado.";
}

