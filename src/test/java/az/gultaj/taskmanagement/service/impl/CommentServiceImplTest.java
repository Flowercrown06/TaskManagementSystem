package az.gultaj.taskmanagement.service.impl;

import az.gultaj.taskmanagement.dto.request.CommentRequest;
import az.gultaj.taskmanagement.dto.response.CommentResponse;
import az.gultaj.taskmanagement.entity.Comment;
import az.gultaj.taskmanagement.entity.Task;
import az.gultaj.taskmanagement.entity.User;
import az.gultaj.taskmanagement.exception.ResourceNotFoundException;
import az.gultaj.taskmanagement.mapper.CommentMapper;
import az.gultaj.taskmanagement.repository.CommentRepository;
import az.gultaj.taskmanagement.repository.TaskRepository;
import az.gultaj.taskmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User mockUser;
    private Task mockTask;
    private Comment mockComment;
    private CommentRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setEmail("gultaj@gmail.com");

        mockTask = new Task();
        mockTask.setId(10L);

        mockRequest = new CommentRequest();
        mockRequest.setTaskId(10L);
        mockRequest.setContent("Test rəyi");

        mockComment = new Comment();
        mockComment.setId(1L);
        mockComment.setContent("Test rəyi");
    }

    @Test
    void addComment_WhenUserAndTaskExist_ShouldCreateComment() {
        // Given
        String email = "gultaj@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(taskRepository.findById(10L)).thenReturn(Optional.of(mockTask));
        when(commentMapper.toEntity(any(CommentRequest.class))).thenReturn(mockComment);
        when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);
        when(commentMapper.toResponse(any(Comment.class))).thenReturn(new CommentResponse());

        // When
        CommentResponse response = commentService.addComment(mockRequest);

        // Then
        assertNotNull(response);
        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(commentMapper).toEntity(mockRequest);
    }

    @Test
    void addComment_WhenTaskNotFound_ShouldThrowResourceNotFoundException() {
        // Given
        String email = "gultaj@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(taskRepository.findById(10L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.addComment(mockRequest);
        });

        verify(commentRepository, never()).save(any());
    }
}