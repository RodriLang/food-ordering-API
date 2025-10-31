package com.group_three.food_ordering.notifications.services;

import com.group_three.food_ordering.notifications.SseEventType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class SseService {

    private static final long TIMEOUT = 600_000L; // 10 minutos
    private static final int MAX_EMITTERS_PER_SESSION = 20;

    private final Map<String, List<EmitterWrapper>> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String tableSessionId) {
        log.debug("[SseService] Subscribing to table session {}", tableSessionId);
        SseEmitter emitter = new SseEmitter(TIMEOUT);

        List<EmitterWrapper> sessionEmitters = emitters.computeIfAbsent(
                tableSessionId,
                k -> new CopyOnWriteArrayList<>()
        );

        if (sessionEmitters.size() >= MAX_EMITTERS_PER_SESSION) {
            log.warn("[SseService] Max emitters reached for session {}. Rejecting new subscription.", tableSessionId);
            throw new IllegalStateException("Too many clients subscribed");
        }

        EmitterWrapper wrapper = new EmitterWrapper(emitter);
        sessionEmitters.add(wrapper);

        Runnable cleanupCallback = () -> removeEmitter(tableSessionId, wrapper);

        emitter.onCompletion(cleanupCallback);
        emitter.onTimeout(cleanupCallback);
        emitter.onError(e -> {
            log.warn("[SseService] Emitter error for session {}: {}", tableSessionId, e.getMessage());
            cleanupCallback.run();
        });

        try {
            emitter.send(SseEmitter.event()
                    .name(SseEventType.CONNECTION_SUCCESSFUL.getEventName())
                    .data("Connected to Table session " + tableSessionId));
        } catch (IOException e) {
            log.warn("[SseService] Failed initial send: {}", e.getMessage());
            cleanupCallback.run();
        }

        return emitter;
    }

    public void sendEventToTableSession(String tableSessionId, SseEventType eventType, Object data) {
        List<EmitterWrapper> sessionEmitters = emitters.get(tableSessionId);
        if (sessionEmitters == null || sessionEmitters.isEmpty()) return;

        log.debug("[SseService] Sending event {} to {} clients for session {}",
                eventType.getEventName(), sessionEmitters.size(), tableSessionId);

        for (EmitterWrapper wrapper : sessionEmitters) {
            try {
                wrapper.getEmitter().send(SseEmitter.event()
                        .name(eventType.getEventName())
                        .data(data));
                wrapper.refreshLastActive();
            } catch (Exception e) {
                log.warn("[SseService] Failed to send to one emitter: {}", e.getMessage());
            }
        }
    }

    private void removeEmitter(String sessionId, EmitterWrapper wrapper) {
        List<EmitterWrapper> list = emitters.get(sessionId);
        if (list != null) {
            list.remove(wrapper);
            if (list.isEmpty()) {
                emitters.remove(sessionId);
                log.debug("[SseService] Removed empty session {}", sessionId);
            }
        }
    }

    /** Limpieza periódica de conexiones muertas o inactivas */
    @Scheduled(fixedRate = 60_000)
    public void cleanInactiveEmitters() {
        emitters.forEach((sessionId, list) -> {
            list.removeIf(EmitterWrapper::isExpired);
            if (list.isEmpty()) emitters.remove(sessionId);
        });
    }

    private static class EmitterWrapper {
        @Getter
        private final SseEmitter emitter;
        private Instant lastActive;

        public EmitterWrapper(SseEmitter emitter) {
            this.emitter = emitter;
            this.lastActive = Instant.now();
        }

        public void refreshLastActive() {
            this.lastActive = Instant.now();
            try {
                emitter.send(SseEmitter.event().name("ping").data("keep-alive"));
            } catch (IOException e) {
                log.trace("[SseService] Ping failed: {}", e.getMessage());
            }
        }

        public boolean isExpired() {
            return Instant.now().minusMillis(TIMEOUT).isAfter(lastActive);
        }
    }
}
