package com.group_three.food_ordering.context;

import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.repositories.FoodVenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
public class TenantContext {

    private final FoodVenueRepository foodVenueRepository;

    private UUID currentFoodVenueId;
    private FoodVenue currentFoodVenue;

    public void setCurrentFoodVenueId(String tenantId) {
        this.currentFoodVenueId = UUID.fromString(tenantId);
    }

    public FoodVenue getCurrentFoodVenue() {
        if (currentFoodVenue == null && currentFoodVenueId != null) {
            currentFoodVenue = foodVenueRepository.findById(currentFoodVenueId)
                    .orElseThrow(() -> new EntityNotFoundException("FoodVenue", currentFoodVenueId.toString()));
        }
        return currentFoodVenue;
    }

    public UUID getCurrentFoodVenueId() {
        return getCurrentFoodVenue().getId();
    }
}
