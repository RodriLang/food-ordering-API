package com.group_three.food_ordering.context;

import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.exceptions.MissingTenantContextException;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.repositories.FoodVenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
public class TenantContext {

    private final FoodVenueRepository foodVenueRepository;

    private UUID currentFoodVenueId;
    private FoodVenue currentFoodVenue;

    public void setCurrentFoodVenueId(String tenantId) {
        try {
            this.currentFoodVenueId = UUID.fromString(tenantId);
            log.debug("[TenantContext] Set currentFoodVenueId={}", tenantId);
        } catch (Exception e) {
            this.currentFoodVenueId = null;
            log.warn("[TenantContext] Invalid tenantId={} (not a valid UUID)", tenantId);
        }
    }

    public FoodVenue getCurrentFoodVenue() {
        if (currentFoodVenue == null && currentFoodVenueId != null) {
            log.debug("[TenantContext] Fetching FoodVenue id={}", currentFoodVenueId);
            currentFoodVenue = foodVenueRepository.findById(currentFoodVenueId)
                    .orElseThrow(() -> new EntityNotFoundException("FoodVenue", currentFoodVenueId.toString()));
            log.info("[TenantContext] Loaded FoodVenue id={} name={}", currentFoodVenueId, currentFoodVenue.getName());
        }
        return currentFoodVenue;
    }

    public UUID getCurrentFoodVenueId() {
        return getCurrentFoodVenue().getId();
    }

    public FoodVenue determineCurrentFoodVenue() {
        if (getCurrentFoodVenue() == null) {
            log.warn("[TenantContext] No tenant context resolved for this request");
            throw new MissingTenantContextException();
        }
        return currentFoodVenue;
    }
}
