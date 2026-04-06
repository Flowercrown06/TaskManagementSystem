package az.company.bestpractices.scheduler;

import az.company.bestpractices.entity.Task;
import az.company.bestpractices.enums.TaskStatus;
import az.company.bestpractices.repository.TaskRepository;
import az.company.bestpractices.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskDeadlineScheduler {

    private final TaskRepository taskRepository;
    private final NotificationService notificationService;

    // Hər 30 dəqiqədən bir yoxlasın
    @Scheduled(cron = "0 0/30 * * * *")
    @Transactional
    public void checkDeadlines() {
        log.info("Deadline və xatırlatma yoxlanışı başladı...");
        LocalDateTime now = LocalDateTime.now();

        // 1. GECİKƏNLƏRİ TAP VƏ BİLDİRİŞ AT
        List<Task> overdueTasks = taskRepository.findAllByStatusNotAndDeadlineBefore(TaskStatus.DONE, now);
        for (Task task : overdueTasks) {
            String msg = "GECİKMƏ: '" + task.getTitle() + "' tapşırığının vaxtı bitib!";

            // İstifadəçiyə (əgər assign olunubsa)
            if (task.getUser() != null) {
                notificationService.sendNotification(task.getUser().getId(), msg);
            }
            // Adminə də bildiriş getsin (məsələn, ID-si 1 olan Admin)
            log.warn(msg);
        }

        // 2. 1 SAATI QALANLARI TAP (Xatırlatma)
        LocalDateTime oneHourLater = now.plusHours(1);
        LocalDateTime oneHourAndFiveMinsLater = now.plusHours(1).plusMinutes(5);

        List<Task> soonDueTasks = taskRepository.findTasksDueInAnHour(oneHourLater, oneHourAndFiveMinsLater);

        for (Task task : soonDueTasks) {
            if (task.getUser() != null) {
                notificationService.sendNotification(
                        task.getUser().getId(),
                        "XATIRLATMA: '" + task.getTitle() + "' bitməsinə 1 saat qaldı!"
                );
            }
        }
    }
}