package com.group_three.food_ordering.configs;

public class ApiPaths {

    private ApiPaths() {
    }

    public static final String URI_BASE_V1 = "/api/v1";
    public static final String PUBLIC_URI = URI_BASE_V1 + "/public";
    public static final String AUTH_URI = URI_BASE_V1 + "/auth";
    public static final String ROLE_SELECTOR_URI = AUTH_URI + "/switch-roles";
    public static final String CURRENT_URI = URI_BASE_V1 + "/me";
    public static final String VENUE_URI = URI_BASE_V1 + "/food-venues";
    public static final String TABLE_URI = URI_BASE_V1 + "/tables";
    public static final String TABLE_SESSION_URI = URI_BASE_V1 + "/table-sessions";
    public static final String PARTICIPANT_URI = URI_BASE_V1 + "/participants";
    public static final String TAG_URI = URI_BASE_V1 + "/tags";
    public static final String MENU_URI = URI_BASE_V1 + "/menus";
    public static final String CATEGORY_URI = URI_BASE_V1 + "/categories";
    public static final String PRODUCT_URI = URI_BASE_V1 + "/products";
    public static final String FEAT_PRODUCT_URI = PRODUCT_URI + "/featured";
    public static final String ORDER_URI = URI_BASE_V1 + "/orders";
    public static final String PAYMENT_URI = URI_BASE_V1 + "/payments";
    public static final String EMPLOYMENT_URI = URI_BASE_V1 + "/employments";
    public static final String ROOT_ACCESS_URI = URI_BASE_V1 + "/root";
    public static final String ADMIN_URI = ROOT_ACCESS_URI + "/admins";
    public static final String ROOT_USER_URI = ROOT_ACCESS_URI + "/users";

}

