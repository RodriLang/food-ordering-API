package com.group_three.food_ordering.enums;

public enum CloudinaryFolder {
    USERS("users"),
    FOOD_VENUES("foodVenues"),
    PRODUCTS("products"),
    CATEGORIES("categories");

    private final String folderName;

    CloudinaryFolder(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }
}