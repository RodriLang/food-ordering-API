package com.group_three.food_ordering.context;

import java.util.UUID;

@Component
public class TenantContext {

    private final IFoodVenueRepository foodVenueRepository;
    private FoodVenue cachedVenue;

    public TenantContext(FoodVenueRepository foodVenueRepository) {
        this.foodVenueRepository = foodVenueRepository;
    }

    public FoodVenue getCurrentFoodVenue() {
        if (cachedVenue == null) {
            cachedVenue = foodVenueRepository.findAll().stream()
                .filter(venue -> "Burger House".equalsIgnoreCase(venue.getName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se encontró el FoodVenue 'Burger House'"));
        }
        return cachedVenue;
    }
}
