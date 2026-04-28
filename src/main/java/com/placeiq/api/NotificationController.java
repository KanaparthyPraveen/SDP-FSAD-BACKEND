package com.placeiq.api;

import com.placeiq.dto.ApiResponse;
import com.placeiq.model.Notification;
import com.placeiq.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /** SSE Stream — frontend connects once on login */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@RequestParam String userId) {
        return notificationService.subscribe(userId);
    }

    /** Get all notifications for a user */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Notification>>> getAll(@RequestParam String userId) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getForUser(userId)));
    }

    /** Unread count */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> unreadCount(@RequestParam String userId) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.unreadCount(userId)));
    }

    /** Mark one notification as read */
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Notification>> markRead(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.markRead(id)));
    }

    /** Mark all as read */
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<String>> markAllRead(@RequestParam String userId) {
        notificationService.markAllRead(userId);
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read"));
    }
}
