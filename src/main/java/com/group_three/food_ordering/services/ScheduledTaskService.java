package com.group_three.food_ordering.services; // O el paquete que prefieras

import com.group_three.food_ordering.enums.EmploymentStatus;
import com.group_three.food_ordering.models.Employment;
import com.group_three.food_ordering.repositories.EmploymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ScheduledTaskService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskService.class);

    private final EmploymentRepository employmentRepository;

    public ScheduledTaskService(EmploymentRepository employmentRepository) {
        this.employmentRepository = employmentRepository;
    }

    /**
     * Tarea programada para limpiar invitaciones de empleo expiradas.
     * Se ejecuta una vez cada hora, en el minuto 0.
     * (ej. 01:00, 02:00, 03:00, etc.)
     */
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void expirePendingInvitations() {
        Instant now = Instant.now();
        logger.info("Ejecutando tarea programada: Buscando invitaciones expiradas en {}", now);

        // 1. Encontrar todas las invitaciones PENDIENTES que ya expiraron
        List<Employment> expiredInvitations = employmentRepository.findByStatusAndTokenExpirationBefore(
                EmploymentStatus.PENDING,
                now
        );

        if (expiredInvitations.isEmpty()) {
            logger.info("No se encontraron invitaciones expiradas.");
            return;
        }

        logger.info("Se encontraron {} invitaciones expiradas. Actualizando...", expiredInvitations.size());

        // 2. Actualizar su estado a EXPIRED e invalidar el token
        for (Employment invitation : expiredInvitations) {
            invitation.setStatus(EmploymentStatus.EXPIRED);
            invitation.setInvitationToken(null); // Invalida el token para que no pueda ser usado
            invitation.setTokenExpiration(null);

            logger.debug("Invitación ID {} marcada como EXPIRED.", invitation.getId());
        }

        employmentRepository.saveAll(expiredInvitations);
        logger.info("Tarea completada: {} invitaciones actualizadas a EXPIRED.", expiredInvitations.size());
    }
}