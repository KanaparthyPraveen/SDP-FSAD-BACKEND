package com.placeiq.service;

import com.placeiq.model.Notification;
import com.placeiq.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // Map of userId → SSE Emitter (thread-safe)
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // ─── SSE Stream ───────────────────────────────────────────────────────────

    public SseEmitter subscribe(String userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));

        emitters.put(userId, emitter);

        // Send a connect confirmation event
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("SSE connected for userId=" + userId));
        } catch (IOException e) {
            emitters.remove(userId);
        }

        return emitter;
    }

    // ─── Create + Push Notification ──────────────────────────────────────────

    public Notification createAndPush(String userId, String type, String title, String message, String link) {
        Notification notification = Notification.create(userId, type, title, message, link);
        notificationRepository.save(notification);
        pushToUser(userId, notification);
        return notification;
    }

    private void pushToUser(String userId, Notification notification) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notification));
            } catch (IOException e) {
                log.warn("Failed to push notification to userId={}: {}", userId, e.getMessage());
                emitters.remove(userId);
            }
        }
    }

    // ─── REST Operations ─────────────────────────────────────────────────────

    public List<Notification> getForUser(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Notification markRead(String id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        n.setRead(true);
        return notificationRepository.save(n);
    }

    public void markAllRead(String userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndReadFalse(userId);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    public long unreadCount(String userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }
}
