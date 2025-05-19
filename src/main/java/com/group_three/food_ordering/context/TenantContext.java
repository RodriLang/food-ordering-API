package com.group_three.food_ordering.context;

import java.util.UUID;

@Component
public class TenantContext {

    private final FoodVenueRepository foodVenueRepository;
    private FoodVenue cachedVenue;

    public TenantContext(FoodVenueRepository foodVenueRepository) {
        this.foodVenueRepository = foodVenueRepository;
    }

    public FoodVenue getCurrentFoodVenue() {
        if (cachedVenue == null) {
            cachedVenue = foodVenueRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No FoodVenue found in DB"));
        }
        return cachedVenue;
    }
}