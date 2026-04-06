package az.company.bestpractices.service.impl;

import az.company.bestpractices.dto.response.NotificationResponse;
import az.company.bestpractices.entity.Notification;
import az.company.bestpractices.entity.User;
import az.company.bestpractices.exception.AccessDeniedException;
import az.company.bestpractices.mapper.NotificationMapper;
import az.company.bestpractices.repository.NotificationRepository;
import az.company.bestpractices.repository.UserRepository;
import az.company.bestpractices.security.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User mockUser;
    private Notification mockNotification;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("gultaj@gmail.com");

        mockNotification = new Notification();
        mockNotification.setId(100L);
        mockNotification.setMessage("Test mesajı");
        mockNotification.setUser(mockUser);
        mockNotification.setRead(false);
    }

    @Test
    void getUserNotifications_Success() {
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            // Given
            String email = "gultaj@gmail.com";
            securityUtil.when(SecurityUtil::getCurrentUserEmail).thenReturn(email);

            // NotificationRepository-də olan metoduna uyğun mock et (məs: findAllByUserEmail...)
            when(notificationRepository.findAllByUserEmailOrderByCreatedAtDesc(email))
                    .thenReturn(List.of(mockNotification));
            when(notificationMapper.toResponseList(any())).thenReturn(List.of(new NotificationResponse()));

            // When
            List<NotificationResponse> result = notificationService.getUserNotifications();

            // Then
            assertNotNull(result);
            verify(notificationRepository).findAllByUserEmailOrderByCreatedAtDesc(email);
        }
    }

    @Test
    void markAsRead_Success() {
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            // Given
            securityUtil.when(SecurityUtil::getCurrentUserEmail).thenReturn("gultaj@gmail.com");
            when(notificationRepository.findById(100L)).thenReturn(Optional.of(mockNotification));

            // When
            notificationService.markAsRead(100L);

            // Then
            assertTrue(mockNotification.isRead());
            verify(notificationRepository).save(mockNotification);
        }
    }

    @Test
    void markAsRead_WhenNotOwner_ShouldThrowException() {
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            // Given: Başqa bir userin emailini simulyasiya edirik
            securityUtil.when(SecurityUtil::getCurrentUserEmail).thenReturn("other@gmail.com");
            when(notificationRepository.findById(100L)).thenReturn(Optional.of(mockNotification));

            // When & Then: Başqasının bildirişini oxumağa çalışanda AccessDenied atmalıdır
            assertThrows(AccessDeniedException.class, () -> notificationService.markAsRead(100L));
        }
    }
}