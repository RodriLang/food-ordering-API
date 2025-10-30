package com.group_three.food_ordering.notifications;

import com.group_three.food_ordering.utils.constants.ApiPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_CLIENT')")
@RestController
@RequestMapping(ApiPaths.EVENTS_SUBSCRIPTIONS)
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;

    @GetMapping("/table-sessions/{tableSessionId}")
    public SseEmitter subscribeToTableSession(@PathVariable String tableSessionId) {
        // Llama al servicio para crear y guardar el emitter
        return sseService.subscribe(tableSessionId);
    }
}