package com.group_three.food_ordering.context;

import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.repositories.IFoodVenueRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class TenantContext {

    private final IFoodVenueRepository foodVenueRepository;
    private FoodVenue cachedVenue;

    public TenantContext(IFoodVenueRepository foodVenueRepository) {
        this.foodVenueRepository = foodVenueRepository;
    }

    public FoodVenue getCurrentFoodVenue() {
        if (cachedVenue == null) {
            String email = "contact@burgerhouse.com";

            cachedVenue = foodVenueRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new RuntimeException("No se encontró el FoodVenue con email: " + email));
        }
        return cachedVenue;
    }
}
