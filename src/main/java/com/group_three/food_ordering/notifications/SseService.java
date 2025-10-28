package com.group_three.food_ordering.notifications;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class SseService {

    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String tableSessionId) {
        log.debug("[SseService] Subscribing to table session {}", tableSessionId);
        SseEmitter emitter = new SseEmitter(600_000L);

        List<SseEmitter> sessionEmitters = this.emitters.computeIfAbsent(
                tableSessionId,
                k -> new CopyOnWriteArrayList<>()
        );

        sessionEmitters.add(emitter);

        // 👇 ESTA LÓGICA DE LIMPIEZA ES LA QUE DEBE ENCARGARSE DE REMOVER
        Runnable cleanupCallback = () -> {
            log.debug("[SseService] Emitter cleaning up for session {}", tableSessionId);
            sessionEmitters.remove(emitter);
            if (sessionEmitters.isEmpty()) {
                this.emitters.remove(tableSessionId);
                log.debug("[SseService] No emitters left for session {}, removing map entry", tableSessionId);
            }
        };

        emitter.onCompletion(cleanupCallback);
        emitter.onTimeout(cleanupCallback);
        emitter.onError((e) -> {
            log.warn("[SseService] SseEmitter error callback triggered for session {}: {}", tableSessionId, e.getMessage());
            cleanupCallback.run(); // Llama a la limpieza cuando el emitter MISMO reporta un error irrecuperable
        });

        // ... (envío del evento de conexión) ...
        try {
            emitter.send(SseEmitter.event()
                    .name(SseEventType.CONNECTION_SUCCESSFUL.getEventName())
                    .data("Connected to Table session " + tableSessionId));
            log.debug("[SseService] SseEmitter established for session {}", tableSessionId);
        } catch (IOException e) {
            log.warn("[SseService] SseEmitter failed to send initial connection event: {}", e.getMessage());
            // Si falla el envío INICIAL, sí lo limpiamos porque la conexión ni siquiera empezó bien.
            cleanupCallback.run();
        }

        return emitter;
    }

    /**
     * Envía un evento a TODOS los clientes suscritos.
     * MODIFICADO: Ya no elimina emitters si falla el envío. Confía en los callbacks.
     */
    public void sendEventToTableSession(String tableSessionId, SseEventType eventType, Object data) {
        log.debug("[SseService] Sending event {} to all emitters for table session {}", eventType.getEventName(), tableSessionId);

        List<SseEmitter> sessionEmitters = emitters.get(tableSessionId);

        if (sessionEmitters == null || sessionEmitters.isEmpty()) {
            log.debug("[SseService] No active emitters found for session {}", tableSessionId);
            return;
        }

        for (SseEmitter emitter : sessionEmitters) {
            try {
                emitter.send(SseEmitter.event().name(eventType.getEventName()).data(data));
            } catch (Exception e) {
                // Si la conexión está realmente rota, el onError/onCompletion/onTimeout
                // del emitter se encargará de llamar a cleanupCallback.
                log.warn("[SseService] Failed to send event {} to one emitter for session {}: {}. Emitter might be disconnected.",
                        eventType.getEventName(), tableSessionId, e.getMessage());
            }
        }
    }
}