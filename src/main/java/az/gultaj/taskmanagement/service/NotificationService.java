package az.gultaj.taskmanagement.service;

import az.gultaj.taskmanagement.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {
    void sendNotification(Long userId, String message);
    List<NotificationResponse> getUserNotifications();
    void markAsRead(Long id);
}