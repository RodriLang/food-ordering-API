package com.group_three.food_ordering.configs;

import com.group_three.food_ordering.dto.AuditorUser;
import com.group_three.food_ordering.services.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<UUID> auditorProvider(AuthService authService) {
        return () -> {
            try {
                AuditorUser currentUser = authService.getAuditorUser();
                return Optional.ofNullable(currentUser).map(AuditorUser::publicId);
            } catch (Exception e) {
                return Optional.empty();
            }
        };
    }
}
