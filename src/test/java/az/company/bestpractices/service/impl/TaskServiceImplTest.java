package az.company.bestpractices.service.impl;

import az.company.bestpractices.dto.request.CreateTaskRequest;
import az.company.bestpractices.dto.response.TaskResponse;
import az.company.bestpractices.entity.Category;
import az.company.bestpractices.entity.Task;
import az.company.bestpractices.entity.User;
import az.company.bestpractices.exception.AlreadyExistsException;
import az.company.bestpractices.mapper.TaskMapper;
import az.company.bestpractices.repository.CategoryRepository;
import az.company.bestpractices.repository.TaskRepository;
import az.company.bestpractices.repository.UserRepository;
import az.company.bestpractices.security.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskMapper taskMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private CreateTaskRequest request;
    private Task task;
    private User user;
    private Category category;

    @BeforeEach
    void setUp() {
        request = new CreateTaskRequest();
        request.setTitle("Test Task");
        request.setCategoryId(1L);

        user = new User();
        user.setEmail("gultaj@example.com");

        category = new Category();
        category.setId(1L);

        task = new Task();
        task.setTitle("Test Task");
        task.setUser(user);
    }

    @Test
    void createTask_Success_Test() {
        // Mockito ilə statik SecurityUtil metodunu simulyasiya edirik
        try (MockedStatic<SecurityUtil> mockedSecurity = mockStatic(SecurityUtil.class)) {
            mockedSecurity.when(SecurityUtil::getCurrentUserEmail).thenReturn("gultaj@example.com");

            // Mock davranışları
            when(taskRepository.existsByTitle(anyString())).thenReturn(false);
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(userRepository.findByEmail("gultaj@example.com")).thenReturn(Optional.of(user));
            when(taskMapper.toEntity(any())).thenReturn(task);
            when(taskRepository.save(any())).thenReturn(task);
            when(taskMapper.toResponse(any())).thenReturn(new TaskResponse());

            // Metodu çağırırıq
            TaskResponse response = taskService.createTask(request);

            // Yoxlamalar
            assertNotNull(response);
            verify(taskRepository, times(1)).save(any());
        }
    }

    @Test
    void createTask_ThrowsAlreadyExistsException_Test() {
        // Başlıq artıq mövcuddursa
        when(taskRepository.existsByTitle(request.getTitle())).thenReturn(true);

        // Xəta atılmasını gözləyirik
        assertThrows(AlreadyExistsException.class, () -> taskService.createTask(request));

        // Save metodunun çağırılmadığından əmin oluruq
        verify(taskRepository, never()).save(any());
    }

    @Test
    void deleteTask_ThrowsException_WhenUserNotOwner() {
        try (MockedStatic<SecurityUtil> mockedSecurity = mockStatic(SecurityUtil.class)) {
            mockedSecurity.when(SecurityUtil::getCurrentUserEmail).thenReturn("bashqa_user@example.com");

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

            // Başqasının tapşırığını silməyə çalışanda xəta atmalıdır
            RuntimeException exception = assertThrows(RuntimeException.class, () -> taskService.deleteTask(1L));

            assertEquals("Siz yalnız öz tapşırıqlarınızı silə bilərsiniz!", exception.getMessage());
            verify(taskRepository, never()).delete(any());
        }
    }
}