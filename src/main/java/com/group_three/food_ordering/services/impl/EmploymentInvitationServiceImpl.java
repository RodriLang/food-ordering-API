package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.enums.EmploymentStatus;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.exceptions.InvalidInvitationException;
import com.group_three.food_ordering.models.Employment;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.notifications.services.EmailService;
import com.group_three.food_ordering.repositories.EmploymentRepository;
import com.group_three.food_ordering.repositories.FoodVenueRepository;
import com.group_three.food_ordering.repositories.UserRepository;
import com.group_three.food_ordering.services.EmploymentInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmploymentInvitationServiceImpl implements EmploymentInvitationService {

    private final EmploymentRepository employmentRepository;
    private final UserRepository userRepository;
    private final FoodVenueRepository foodVenueRepository;
    private final EmailService emailService;

    /**
     * Lógica principal para crear y enviar una invitación.
     * Esto sería llamado por un controlador de Admin.
     */
    @Transactional
    public void createInvitation(Employment employment) {

        emailService.sendEmploymentInvitation(employment);
    }

    @Transactional
    public String processResponse(String token, EmploymentStatus responseStatus) {
        Employment employment = employmentRepository.findByInvitationToken(token)
                .orElseThrow(() -> new RuntimeException("Invitación no válida o ya utilizada."));

        // 1. Verificar si ya fue respondida
        if (employment.getStatus() != EmploymentStatus.PENDING) {
            throw new InvalidInvitationException("Esta invitación ya fue procesada.");
        }

        // 2. Verificar expiración
        if (Instant.now().isAfter(employment.getTokenExpiration())) {
            employment.setStatus(EmploymentStatus.EXPIRED);
            employmentRepository.save(employment);
            throw new InvalidInvitationException("Esta invitación ha expirado.");
        }

        // 3. Procesar la respuesta
        if (responseStatus == EmploymentStatus.ACCEPTED) {
            employment.setStatus(EmploymentStatus.ACCEPTED);
            employment.setActive(true);
            employment.setInvitationToken(null); // Invalida el token
            employment.setTokenExpiration(null);
            employmentRepository.save(employment);
            return "¡Gracias! Has aceptado la oferta de trabajo en " + employment.getFoodVenue().getName() + ".";
        } else {
            employment.setStatus(EmploymentStatus.DECLINED);
            employment.setInvitationToken(null); // Invalida el token
            employment.setTokenExpiration(null);
            employmentRepository.save(employment);
            return "Has rechazado la oferta de trabajo.";
        }
    }
}