package com.group_three.food_ordering.context;

import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.repositories.IFoodVenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
public class TenantContext {

    private final IFoodVenueRepository foodVenueRepository;

    private UUID currentTenantId;
    private FoodVenue currentTenant;

    public void setCurrentTenantId(String tenantId) {
        this.currentTenantId = UUID.fromString(tenantId);
    }

    public FoodVenue getCurrentTenant() {
        if (currentTenant == null && currentTenantId != null) {
            currentTenant = foodVenueRepository.findById(currentTenantId)
                    .orElseThrow(() -> new RuntimeException("FoodVenue no encontrado con ID: " + currentTenantId));
        }
        return currentTenant;
    }

    public UUID getCurrentTenantId() {
        return getCurrentTenant().getId();
    }
}
