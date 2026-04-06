package az.gultaj.taskmanagement.service.impl;

import az.gultaj.taskmanagement.dto.request.ReviewRequest;
import az.gultaj.taskmanagement.entity.Review;
import az.gultaj.taskmanagement.entity.Task;
import az.gultaj.taskmanagement.entity.User;
import az.gultaj.taskmanagement.enums.TaskStatus;
import az.gultaj.taskmanagement.mapper.ReviewMapper;
import az.gultaj.taskmanagement.repository.ReviewRepository;
import az.gultaj.taskmanagement.repository.TaskRepository;
import az.gultaj.taskmanagement.repository.UserRepository;
import az.gultaj.taskmanagement.service.MailService;
import az.gultaj.taskmanagement.util.QrCodeGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReviewMapper reviewMapper;
    @Mock
    private MailService mailService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private MockedStatic<QrCodeGenerator> mockedQrGenerator;
    private Task mockTask;
    private User mockReviewer;
    private User mockTaskUser;

    @BeforeEach
    void setUp() {
        mockedQrGenerator = mockStatic(QrCodeGenerator.class);

        mockTaskUser = new User();
        mockTaskUser.setEmail("student@gmail.com");
        mockTaskUser.setFullName("Tələbə Gultaj");

        mockTask = new Task();
        mockTask.setId(1L);
        mockTask.setTitle("Final Project");
        mockTask.setUser(mockTaskUser);
        mockTask.setStatus(TaskStatus.REVIEW);

        mockReviewer = new User();
        mockReviewer.setEmail("manager@gmail.com");
        mockReviewer.setFullName("Müəllimə");
    }

    @AfterEach
    void tearDown() {
        mockedQrGenerator.close();
    }

    @Test
    void completeReview_WhenApproved_ShouldSetStatusDoneAndSendEmail() throws Exception {
        // Given
        ReviewRequest request = new ReviewRequest();
        request.setApproved(true);
        request.setFeedback("Əla işdir!");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(userRepository.findByEmail("manager@gmail.com")).thenReturn(Optional.of(mockReviewer));
        when(QrCodeGenerator.generateQrCode(anyString())).thenReturn(new byte[]{1, 2, 3});
        when(reviewRepository.save(any(Review.class))).thenReturn(new Review());

        // When
        reviewService.completeReview(1L, request, "manager@gmail.com");

        // Then
        assertEquals(TaskStatus.DONE, mockTask.getStatus());
        verify(mailService, times(1)).sendEmailWithAttachment(anyString(), anyString(), anyString(), any(), anyString());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void completeReview_WhenRejected_ShouldSetStatusInProgress() {
        // Given
        ReviewRequest request = new ReviewRequest();
        request.setApproved(false);
        request.setFeedback("Yenidən baxın.");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(userRepository.findByEmail("manager@gmail.com")).thenReturn(Optional.of(mockReviewer));
        when(reviewRepository.save(any(Review.class))).thenReturn(new Review());

        // When
        reviewService.completeReview(1L, request, "manager@gmail.com");

        // Then
        assertEquals(TaskStatus.IN_PROGRESS, mockTask.getStatus());
        // Rədd edilibsə email və QR kod göndərilməməlidir
        verify(mailService, never()).sendEmailWithAttachment(any(), any(), any(), any(), any());
    }
}