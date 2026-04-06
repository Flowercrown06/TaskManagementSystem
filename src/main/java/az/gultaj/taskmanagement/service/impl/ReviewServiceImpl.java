package az.gultaj.taskmanagement.service.impl;

import az.gultaj.taskmanagement.dto.request.ReviewRequest;
import az.gultaj.taskmanagement.dto.response.ReviewResponse;
import az.gultaj.taskmanagement.entity.Review;
import az.gultaj.taskmanagement.entity.Task;
import az.gultaj.taskmanagement.entity.User;
import az.gultaj.taskmanagement.enums.TaskStatus;
import az.gultaj.taskmanagement.exception.AccessDeniedException;
import az.gultaj.taskmanagement.exception.ResourceNotFoundException;
import az.gultaj.taskmanagement.exception.UserNotFoundException;
import az.gultaj.taskmanagement.mapper.ReviewMapper;
import az.gultaj.taskmanagement.repository.ReviewRepository;
import az.gultaj.taskmanagement.repository.TaskRepository;
import az.gultaj.taskmanagement.repository.UserRepository;
import az.gultaj.taskmanagement.security.SecurityUtil;
import az.gultaj.taskmanagement.service.MailService;
import az.gultaj.taskmanagement.service.NotificationService;
import az.gultaj.taskmanagement.service.ReviewService;
import az.gultaj.taskmanagement.util.QrCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;
    private final MailService mailService;
    private final NotificationService notificationService;

    @Override
    public void submitTaskForReview(Long taskId) {
        log.info("Tapşırıq yoxlanışa göndərilir. ID: {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.error("Tapşırıq tapılmadı! ID: {}", taskId);
                    return new ResourceNotFoundException("Tapşırıq tapılmadı ID: " + taskId);
                });

        task.setStatus(TaskStatus.REVIEW);
        taskRepository.save(task);

        log.info("Tapşırıq statusu 'REVIEW' olaraq dəyişdirildi. ID: {}", taskId);
    }

    @Override
    @Transactional
    public ReviewResponse completeReview(Long taskId, ReviewRequest request, String reviewerEmail) {
        log.info("Yoxlanış prosesi tamamlanır. Task ID: {}, Qərar: {}", taskId, request.getApproved());

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tapşırıq tapılmadı ID: " + taskId));

        User reviewer = userRepository.findByEmail(reviewerEmail)
                .orElseThrow(() -> new UserNotFoundException("Yoxlayıcı tapılmadı Email: " + reviewerEmail));

        boolean isApproved = Boolean.TRUE.equals(request.getApproved());

         task.setStatus(isApproved ? TaskStatus.DONE : TaskStatus.IN_PROGRESS);
        taskRepository.save(task);

        Review review = Review.builder()
                .feedback(request.getFeedback())
                .approved(request.getApproved())
                .task(task)
                .reviewer(reviewer)
                .reviewedAt(LocalDateTime.now())
                .build();
        Review savedReview = reviewRepository.save(review);

        sendSystemNotification(task, request);

        if (isApproved) {
            handleEmailAndQrCode(task, reviewer, taskId);
        } else {
            sendRejectionEmail(task, request.getFeedback());
        }

        return reviewMapper.toResponse(savedReview);
    }

    private void sendRejectionEmail(Task task, String feedback) {
        try {
            log.info("Rədd emaili hazırlanır. Alıcı: {}", task.getUser().getEmail());

            String subject = "Tapşırıq haqqında rəy: Yenidən işlənmə tələb olunur";
            String content = String.format(
                    "Salam %s,\n\n" +
                            "Təəssüf ki, '%s' başlıqlı tapşırığınız menecer tərəfindən təsdiqlənmədi.\n\n" +
                            "Yoxlanış qeydi: %s\n\n" +
                            "Zəhmət olmasa, lazımi düzəlişləri edib tapşırığı yenidən yoxlanışa göndərin.",
                    task.getUser().getFullName(),
                    task.getTitle(),
                    feedback
            );

            mailService.sendEmail(task.getUser().getEmail(), subject, content);
            log.info("Rədd emaili uğurla göndərildi.");
        } catch (Exception e) {
            log.error("Rədd emaili göndərilərkən xəta baş verdi: {}", e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getReviewByTaskId(Long taskId) {
        String currentUserEmail = SecurityUtil.getCurrentUserEmail();
        log.debug("İstifadəçi {} tərəfindən Task ID {} üçün rəy axtarılır", currentUserEmail, taskId);

        // 1. Taskı tapırıq (Sahibini yoxlamaq üçün lazımdır)
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tapşırıq tapılmadı: " + taskId));

        // 2. TƏHLÜKƏSİZLİK YOXALMASI (IDOR Protection)
        // Əgər imtiyazlı (Admin/Super Admin) DEYİLSƏ və taskın SAHİBİ DEYİLSƏ:
        if (!SecurityUtil.isPrivileged() && !task.getUser().getEmail().equals(currentUserEmail)) {
            log.warn("İcazəsiz giriş cəhdi! User: {}, Task ID: {}", currentUserEmail, taskId);
            throw new AccessDeniedException("Siz yalnız öz tapşırıqlarınızın rəylərinə baxa bilərsiniz!");
        }

        // 3. Rəyi gətiririk
        return reviewRepository.findByTaskId(taskId)
                .map(reviewMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Bu tapşırıq üçün rəy tapılmadı: " + taskId));
    }

    @Override
    public List<ReviewResponse> getAllReviews() {
        log.info("Bütün rəylər siyahısı gətirilir");
        return reviewMapper.toResponseList(reviewRepository.findAll());
    }


    private void sendSystemNotification(Task task, ReviewRequest request) {
        try {
            String message = Boolean.TRUE.equals(request.getApproved())
                    ? String.format("Təbrik edirik! '%s' tapşırığınız təsdiqləndi.", task.getTitle())
                    : String.format("Tapşırığınız rədd edildi: %s. Feedback: %s", task.getTitle(), request.getFeedback());

            notificationService.sendNotification(task.getUser().getId(), message);
            log.info("İstifadəçiyə sistem bildirişi göndərildi. User: {}", task.getUser().getEmail());
        } catch (Exception e) {
            log.warn("Bildiriş göndərilərkən xəta baş verdi, amma əsas proses davam edir: {}", e.getMessage());
        }
    }

    private void handleEmailAndQrCode(Task task, User reviewer, Long taskId) {
        try {
            log.info("QR kod və email hazırlanır. Alıcı: {}", task.getUser().getEmail());

            String qrContent = String.format("Verification:\nTask: %s\nReviewer: %s\nDate: %s",
                    task.getTitle(), reviewer.getFullName(), LocalDateTime.now());

            byte[] qrCode = QrCodeGenerator.generateQrCode(qrContent);

            mailService.sendEmailWithAttachment(
                    task.getUser().getEmail(),
                    "Tapşırığınız Təsdiqləndi",
                    "Salam, göndərdiyiniz tapşırıq uğurla yoxlanıldı. QR kodunuz əlavədədir.",
                    qrCode,
                    "verification-qr.png"
            );
            log.info("Email uğurla göndərildi.");
        } catch (Exception e) {
            log.error("Email/QR prosesində xəta: {}", e.getMessage());
        }
    }
}