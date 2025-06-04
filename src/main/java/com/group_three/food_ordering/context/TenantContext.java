package com.group_three.food_ordering.context;

import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.repositories.IFoodVenueRepository;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Setter
@Component
public class TenantContext {

    private final IFoodVenueRepository foodVenueRepository;
    private FoodVenue cachedVenue;
    private String cachedVenueId;

    public TenantContext(IFoodVenueRepository foodVenueRepository) {
        this.foodVenueRepository = foodVenueRepository;
    }

    public FoodVenue getCurrentFoodVenue() {
        if (cachedVenue == null) {
            String email = "contact@burgerhouse.com";

            cachedVenue = foodVenueRepository.findById(UUID.fromString(cachedVenueId))
                    .orElseThrow(() -> new RuntimeException("No se encontró el FoodVenue con email: " + email));
        }
        return cachedVenue;
    }

    public UUID getCurrentFoodVenueId() {
        return getCurrentFoodVenue().getId();
    }

}
