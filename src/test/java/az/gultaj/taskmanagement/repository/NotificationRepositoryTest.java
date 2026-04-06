package az.gultaj.taskmanagement.repository;

import az.gultaj.taskmanagement.entity.Notification;
import az.gultaj.taskmanagement.entity.User;
import az.gultaj.taskmanagement.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class NotificationRepositoryTest {

    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;

    @BeforeEach
    void setUp() {
        // Bildirişin sahibi olan User-i yaradırıq
        user = new User();
        user.setEmail("gultaj.notif@gmail.com");
        user.setFullName("Gultaj Notification Tester");
        user.setPassword("password123");
        user.setRole(Role.USER);
        user.setDeleted(false);
        entityManager.persist(user);

        // Birinci bildiriş (Köhnə)
        Notification oldNotification = new Notification();
        oldNotification.setMessage("Old message");
        oldNotification.setRead(false);
        oldNotification.setCreatedAt(LocalDateTime.now().minusDays(1)); // 1 gün əvvəl
        oldNotification.setUser(user);
        entityManager.persist(oldNotification);

        // İkinci bildiriş (Yeni)
        Notification newNotification = new Notification();
        newNotification.setMessage("New message");
        newNotification.setRead(false);
        newNotification.setCreatedAt(LocalDateTime.now()); // İndi
        newNotification.setUser(user);
        entityManager.persist(newNotification);

        entityManager.flush();
    }

    @Test
    void findAllByUserIdOrderByCreatedAtDesc_ShouldReturnOrderedNotifications() {
        // Action
        List<Notification> notifications = notificationRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId());

        // Assertion
        assertThat(notifications).hasSize(2);

        // İlk gələn bildiriş daha yeni olmalıdır (OrderByCreatedAtDesc)
        assertThat(notifications.get(0).getMessage()).isEqualTo("New message");
        assertThat(notifications.get(1).getMessage()).isEqualTo("Old message");

        // Zaman yoxlaması
        assertThat(notifications.get(0).getCreatedAt())
                .isAfter(notifications.get(1).getCreatedAt());
    }

    @Test
    void findAllByUserIdOrderByCreatedAtDesc_WhenUserHasNoNotifications_ShouldReturnEmptyList() {
        // Action - Mövcud olmayan bir User ID (məsələn 999)
        List<Notification> notifications = notificationRepository.findAllByUserIdOrderByCreatedAtDesc(999L);

        // Assertion
        assertThat(notifications).isEmpty();
    }
}