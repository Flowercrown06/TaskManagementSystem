package az.gultaj.taskmanagement.service.impl;

import az.gultaj.taskmanagement.dto.request.CommentRequest;
import az.gultaj.taskmanagement.dto.response.CommentResponse;
import az.gultaj.taskmanagement.entity.Comment;
import az.gultaj.taskmanagement.entity.Task;
import az.gultaj.taskmanagement.entity.User;
import az.gultaj.taskmanagement.exception.AccessDeniedException;
import az.gultaj.taskmanagement.exception.ResourceNotFoundException;
import az.gultaj.taskmanagement.exception.UserNotFoundException;
import az.gultaj.taskmanagement.mapper.CommentMapper;
import az.gultaj.taskmanagement.repository.CommentRepository;
import az.gultaj.taskmanagement.repository.TaskRepository;
import az.gultaj.taskmanagement.repository.UserRepository;
import az.gultaj.taskmanagement.security.SecurityUtil;
import az.gultaj.taskmanagement.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional //sil
    public CommentResponse addComment(CommentRequest request) {
        // 1. Hazırkı istifadəçinin email-ini avtomatik götürürük
        String currentUserEmail = SecurityUtil.getCurrentUserEmail();
        log.info("İstifadəçi {} tapşırıq {} üçün şərh yazır", currentUserEmail, request.getTaskId());

        // 2. Taskı tapırıq
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Tapşırıq tapılmadı: " + request.getTaskId()));

        // 3. TƏHLÜKƏSİZLİK YOXALMASI
        // Şərt: Əgər müraciət edən Admin/SuperAdmin DEYİLSƏ və Taskın SAHİBİ DEYİLSƏ -> BLOKLA
        if (!SecurityUtil.isPrivileged() && !task.getUser().getEmail().equals(currentUserEmail)) {
            log.warn("İcazəsiz şərh cəhdi! User: {}, Task ID: {}", currentUserEmail, request.getTaskId());
            throw new AccessDeniedException("Siz yalnız öz tapşırıqlarınıza şərh yaza bilərsiniz!");
        }

        // 4. İstifadəçini bazadan tapırıq (Entity-yə set etmək üçün)
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("İstifadəçi tapılmadı"));

        // 5. Şərhi yaradırıq
        Comment comment = commentMapper.toEntity(request);
        comment.setUser(user);
        comment.setTask(task);

        Comment saved = commentRepository.save(comment);
        log.info("Şərh uğurla yaradıldı. ID: {}", saved.getId());

        return commentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)  //sil
    public List<CommentResponse> getCommentsByTask(Long taskId) {
        log.info("Tapşırıq {} üçün şərhlər gətirilir", taskId);

        // 1. Taskı tapırıq
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tapşırıq tapılmadı: " + taskId));

        // 2. TƏHLÜKƏSİZLİK YOXALMASI
        // Şərt: User başqasının taskının şərhlərini oxuya bilməz
        if (!SecurityUtil.isPrivileged() && !task.getUser().getEmail().equals(SecurityUtil.getCurrentUserEmail())) {
            throw new AccessDeniedException("Bu tapşırığın şərhlərinə baxmaq icazəniz yoxdur!");
        }

        List<Comment> comments = commentRepository.findAllByTaskId(taskId);
        return commentMapper.toResponseList(comments);
    }
}