package com.example.smartcommunity.controller;

import com.example.smartcommunity.model.Notification;
import com.example.smartcommunity.model.Pengguna;
import com.example.smartcommunity.service.NotificationService;
import com.example.smartcommunity.service.PenggunaService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final PenggunaService penggunaService;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationController(NotificationService notificationService,
                                   PenggunaService penggunaService,
                                   SimpMessagingTemplate messagingTemplate) {
        this.notificationService = notificationService;
        this.penggunaService = penggunaService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping
    public ResponseEntity<?> getNotifications(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.ok(Map.of("notifications", List.of(), "unreadCount", 0));
        }
        try {
            Pengguna user = penggunaService.findByEmail(authentication.getName());
            List<Notification> notifs = notificationService.getNotificationsByUser(user.getId());
            long unread = notificationService.countUnread(user.getId());
            return ResponseEntity.ok(Map.of("notifications", notifs, "unreadCount", unread));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("notifications", List.of(), "unreadCount", 0));
        }
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.ok(Map.of("count", 0L));
        }
        try {
            Pengguna user = penggunaService.findByEmail(authentication.getName());
            long count = notificationService.countUnread(user.getId());
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("count", 0L));
        }
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Map<String, Boolean>> markAsRead(@PathVariable Long id) {
        try {
            notificationService.markAsRead(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("success", false));
        }
    }

    @PostMapping("/read-all")
    public ResponseEntity<Map<String, Boolean>> markAllAsRead(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.ok(Map.of("success", false));
        }
        try {
            Pengguna user = penggunaService.findByEmail(authentication.getName());
            notificationService.markAllAsRead(user.getId());
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("success", false));
        }
    }
}
