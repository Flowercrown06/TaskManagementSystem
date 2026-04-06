package az.company.bestpractices.service;

import az.company.bestpractices.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {
    void sendNotification(Long userId, String message);
    List<NotificationResponse> getUserNotifications();
    void markAsRead(Long id);
}