package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.dto.request.InvitationRequestDto;
import com.group_three.food_ordering.enums.EmploymentStatus;
import com.group_three.food_ordering.services.EmploymentInvitationService;
import com.group_three.food_ordering.utils.constants.ApiPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(ApiPaths.EMPLOYMENT_INVITATION)
@RequiredArgsConstructor
public class EmploymentInvitationControllerImpl {

    private final EmploymentInvitationService invitationService;

    @PostMapping("/respond")
    public ResponseEntity<Map<String, String>> respondToInvitation(@RequestBody InvitationRequestDto request) {

        EmploymentStatus statusToSet = request.action().equals("accept")
                ? EmploymentStatus.ACCEPTED
                : EmploymentStatus.DECLINED;

        try {
            String message = invitationService.processResponse(request.token(), statusToSet);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
