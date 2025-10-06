package com.group_three.food_ordering.utils;

public class FormatUtils {

    private FormatUtils(){}

    public static String formatOrderNumber(Integer orderNumber) {
        if (orderNumber == null) return null;
        return String.format("%02d", orderNumber);
    }
}
