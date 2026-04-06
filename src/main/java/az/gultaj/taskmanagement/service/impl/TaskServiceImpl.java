package az.gultaj.taskmanagement.service.impl;

import az.gultaj.taskmanagement.dto.request.CreateTaskRequest;
import az.gultaj.taskmanagement.dto.response.TaskResponse;
import az.gultaj.taskmanagement.entity.Category;
import az.gultaj.taskmanagement.entity.Task;
import az.gultaj.taskmanagement.entity.User;
import az.gultaj.taskmanagement.enums.TaskStatus;
import az.gultaj.taskmanagement.exception.*;
import az.gultaj.taskmanagement.mapper.TaskMapper;
import az.gultaj.taskmanagement.repository.CategoryRepository;
import az.gultaj.taskmanagement.repository.TaskRepository;
import az.gultaj.taskmanagement.repository.UserRepository;
import az.gultaj.taskmanagement.security.SecurityUtil;
import az.gultaj.taskmanagement.service.NotificationService;
import az.gultaj.taskmanagement.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;
    private final CategoryRepository  categoryRepository;
    private final NotificationService notificationService;


    @Override
    @Transactional
    public TaskResponse createTask(CreateTaskRequest request) {
        log.info("Yeni tapşırıq yaradılması başladıldı: {}", request.getTitle());
        log.warn("Error ola biler");

        // 1. Başlıq yoxlanışı
        if (taskRepository.existsByTitle(request.getTitle())) {
            log.warn("Tapşırıq başlıq dublikatı: {}", request.getTitle());
            throw new AlreadyExistsException("Bu başlıqlı tapşırıq artıq mövcuddur");
        }

        // 2. Kateqoriya yoxlanışı
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Kateqoriya tapılmadı"));

        // 3. Entity-yə çevirmə (Mapping)
        Task task = taskMapper.toEntity(request);
        task.setCategory(category);
        task.setStatus(TaskStatus.TO_DO);

        // 4. DEADLINE MƏNTİQİ
        if (request.getDeadline() != null) {
            if (request.getDeadline().isBefore(LocalDateTime.now())) {
                log.error("Yanlış deadline cəhdi: {}", request.getDeadline());
                throw new TaskBusinessException("Deadline keçmiş zaman ola bilməz!");
            }
            task.setDeadline(request.getDeadline());
        }

        // 5. ASSIGNMENT (İstifadəçiyə təyin edilmə) MƏNTİQİ
        // Əgər userId gəlibsə assign edirik, gəlməyibsə "boş" (unassigned) qalır
        if (request.getUserId() != null) {
            User assignedUser = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("Tapşırığın təyin ediləcəyi istifadəçi tapılmadı"));

            task.setUser(assignedUser);

            // Tapşırıq kiməsə təyin edildiyi üçün bildiriş göndəririk
            notificationService.sendNotification(
                    assignedUser.getId(),
                    "Sizə yeni tapşırıq təyin edildi: " + task.getTitle()
            );
        } else {
            log.info("Tapşırıq hələlik heç kimə təyin edilmədi (Backlog).");
        }

        // 6. Yadda saxla və cavab qaytar
        Task savedTask = taskRepository.save(task);
        log.info("Tapşırıq uğurla yaradıldı. ID: {}", savedTask.getId());

        return taskMapper.toResponse(savedTask);
    }

    @Transactional
    public void assignTask(Long taskId, Long userId) {
        log.info("Task ID: {} istifadəçi ID: {} üçün təyin edilir", taskId, userId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task tapılmadı"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("İstifadəçi tapılmadı"));

        task.setUser(user);
        // Əgər status hələ də TO_DO deyilsə, bəlkə statusu da yeniləmək olar

        taskRepository.save(task);

        // Və bildiriş göndəririk!
        notificationService.sendNotification(user.getId(), "Sizə yeni bir tapşırıq təyin edildi: " + task.getTitle());
    }

    @Override
    @Transactional
    public TaskResponse updateStatus(Long id, TaskStatus newStatus) {
        log.info("Status yenilənməsi: ID {}, Yeni Status {}", id, newStatus);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tapşırıq tapılmadı: " + id));

        // 1. İcazə Yoxlanışı: Admin/Super Admin deyilsə, sahiblik yoxlanılır
        if (!SecurityUtil.isPrivileged()) {
            // Taskın istifadəçisi yoxdursa və ya email uyğun gəlmirsə -> Blokla
            if (task.getUser() == null || !task.getUser().getEmail().equals(SecurityUtil.getCurrentUserEmail())) {
                log.warn("Yetkisiz status dəyişmə cəhdi! User: {}, Task ID: {}", SecurityUtil.getCurrentUserEmail(), id);
                throw new AccessDeniedException("Siz yalnız özünüzə təyin edilmiş tapşırıqların statusunu dəyişə bilərsiniz!");
            }
        }

        // 2. Biznes Qaydası: Bitmiş tapşırıq geri qaytarıla bilməz (istəsən Adminə icazə verə bilərsən)
        if (task.getStatus() == TaskStatus.DONE && !SecurityUtil.isPrivileged()) {
            throw new IllegalStateException("Bitmiş tapşırıq statusu dəyişdirilə bilməz!");
        }


        task.setStatus(newStatus);
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        log.warn("Tapşırıq silinmə sorğusu. ID: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Silinəcək tapşırıq tapılmadı: " + id));

        // SUPER_ADMIN hər şeyi silə bilər. Digərləri üçün yoxlanış:
        if (!SecurityUtil.hasRole("SUPER_ADMIN")) {
            // Əgər task heç kimə assign olunmayıbsa və ya email fərqlidirsə -> Blokla
            if (task.getUser() == null || !task.getUser().getEmail().equals(SecurityUtil.getCurrentUserEmail())) {
                throw new AccessDeniedException("Bu tapşırığı silmək üçün səlahiyyətiniz yoxdur!");
            }
        }

        taskRepository.delete(task);
        log.info("Tapşırıq uğurla silindi. ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)  //siline bilir
    public TaskResponse getTaskById(Long id) {
        log.debug("Tapşırıq ID ilə axtarılır: {}", id);

        // 1. Taskı tapırıq
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tapşırıq tapılmadı: " + id));

        // 2. SecurityUtil vasitəsilə daxil olan istifadəçini və imtiyazlarını yoxlayırıq
        String currentUserEmail = SecurityUtil.getCurrentUserEmail();

        // 3. Təhlükəsizlik Səddi: Super Admin/Admin DEYİLSƏ və Sahibi DEYİLSƏ -> Blokla
        if (!SecurityUtil.isPrivileged() && !task.getUser().getEmail().equals(currentUserEmail)) {
            log.warn("İcazəsiz giriş cəhdi! User: {}, Task ID: {}", currentUserEmail, id);
            throw new AccessDeniedException("Siz yalnız öz tapşırıqlarınıza baxa bilərsiniz!");
        }

        return taskMapper.toResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasksForAdmin() {
        log.info("Admin tərəfindən bütün tapşırıqlar gətirilir");
        List<Task> tasks = taskRepository.findAll();
        return taskMapper.toResponseList(tasks);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getMyTasks() {
        String email = SecurityUtil.getCurrentUserEmail();
        log.info("İstifadəçi üçün şəxsi tapşırıqlar gətirilir: {}", email);

        List<Task> tasks = taskRepository.findAllByUserEmail(email);
        return taskMapper.toResponseList(tasks);
    }

    @Override
    public boolean isTitleDuplicate(String title) {

        return taskRepository.existsByTitle(title);
    }
}