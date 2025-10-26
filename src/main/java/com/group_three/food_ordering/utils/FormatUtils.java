package com.group_three.food_ordering.utils;

import org.mapstruct.Named;

public class FormatUtils {

    private FormatUtils(){}

    @Named("formatOrderNumber")
    public static String formatOrderNumber(Integer orderNumber) {
        if (orderNumber == null) return null;
        return String.format("%03d", orderNumber);
    }
}
