package com.example.smartcommunity.service;

import com.example.smartcommunity.model.Complaint;
import com.example.smartcommunity.model.Notification;
import com.example.smartcommunity.model.User;

import java.util.List;

public interface NotificationService {
    Notification createNotification(Notification.Type type, String message, User user, Complaint complaint);
    List<Notification> getNotificationsByUser(Long userId);
    List<Notification> getUnreadNotifications(Long userId);
    long countUnread(Long userId);
    void markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
}
