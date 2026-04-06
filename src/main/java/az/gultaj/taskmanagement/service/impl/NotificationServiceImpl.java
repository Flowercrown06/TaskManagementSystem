package az.gultaj.taskmanagement.service.impl;

import az.gultaj.taskmanagement.dto.response.NotificationResponse;
import az.gultaj.taskmanagement.entity.Notification;
import az.gultaj.taskmanagement.entity.User;
import az.gultaj.taskmanagement.exception.AccessDeniedException;
import az.gultaj.taskmanagement.exception.ResourceNotFoundException;
import az.gultaj.taskmanagement.exception.UserNotFoundException;
import az.gultaj.taskmanagement.mapper.NotificationMapper;
import az.gultaj.taskmanagement.repository.NotificationRepository;
import az.gultaj.taskmanagement.repository.UserRepository;
import az.gultaj.taskmanagement.security.SecurityUtil;
import az.gultaj.taskmanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public void sendNotification(Long userId, String message) {
        log.info("İstifadəçiyə bildiriş göndərilir. UserID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("İstifadəçi tapılmadı: " + userId));

        Notification notification = Notification.builder()
                .message(message)
                .user(user)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserNotifications() {
        // 1. Email-i parametr kimi yox, daxildən SecurityUtil ilə alırıq
        String currentUserEmail = SecurityUtil.getCurrentUserEmail();
        log.info("{} üçün bildirişlər gətirilir", currentUserEmail);

        // 2. Yalnız daxil olan istifadəçinin bildirişlərini gətiririk
        List<Notification> notifications = notificationRepository
                .findAllByUserEmailOrderByCreatedAtDesc(currentUserEmail);

        return notificationMapper.toResponseList(notifications);
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        log.debug("Bildiriş oxundu olaraq işarələnir. ID: {}", id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bildiriş tapılmadı: " + id));

        // 3. TƏHLÜKƏSİZLİK SƏDDİ (IDOR Protection)
        // Heç kim başqasının bildirişini "oxundu" edə bilməz!
        String currentUserEmail = SecurityUtil.getCurrentUserEmail();
        if (!notification.getUser().getEmail().equals(currentUserEmail)) {
            log.warn("İcazəsiz bildiriş manipulyasiyası! User: {}, Notification ID: {}", currentUserEmail, id);
            throw new AccessDeniedException("Bu bildirişi dəyişmək icazəniz yoxdur!");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }
}