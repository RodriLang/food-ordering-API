package com.group_three.food_ordering.listener;

import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.services.AuthService;
import com.group_three.food_ordering.configs.SpringContext;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class AuditoryListener {

    private AuthService authService;

    private AuthService getAuthService() {
        if (authService == null) {
            authService = SpringContext.getBean(AuthService.class);
        }
        return authService;
    }

    @PrePersist
    public void setCreatedFields(Object entity) {
        log.debug("[AuditoryListener] setting CreatedFields");
        if (entity instanceof Auditable auditable) {
            auditable.getAuditory().setCreationDate(LocalDateTime.now());

            // Evita recursión infinita cuando se guarda un User
            if (!(entity instanceof User)) {
                try {
                    AuditorUser currentUser = getAuthService().getCurrentUser();
                    if (currentUser != null) {
                        auditable.getAuditory().setCreatedById(currentUser.getPublicId());
                    }
                } catch (Exception e) {
                    log.warn("[AuditoryListener] Could not set createdById: {}", e.getMessage());
                }
            }
        }
    }




    @PreUpdate
    public void setUpdatedFields(Object entity) {
        log.debug("[AuditoryListener] setting UpdatedFields");
        if (entity instanceof Auditable auditable) {
            AuditorUser currentUser = getAuthService().getCurrentUser();
            auditable.getAuditory().setLastUpdateDate(LocalDateTime.now());
            if (currentUser != null)
                auditable.getAuditory().setLastUpdatedById(currentUser.getPublicId());
        }
    }
}
