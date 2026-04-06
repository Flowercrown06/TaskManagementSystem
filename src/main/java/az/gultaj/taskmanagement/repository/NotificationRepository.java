package az.gultaj.taskmanagement.repository;

import az.gultaj.taskmanagement.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUserIdOrderByCreatedAtDesc(Long userId);
    List<Notification> findAllByUserEmailOrderByCreatedAtDesc(String email);
}