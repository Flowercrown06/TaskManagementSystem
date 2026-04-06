package az.gultaj.taskmanagement.controller;

import az.gultaj.taskmanagement.dto.request.ReviewRequest;
import az.gultaj.taskmanagement.dto.response.ReviewResponse;
import az.gultaj.taskmanagement.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
    }

    @Test
    void submitForReview_Success() throws Exception {
        mockMvc.perform(post("/api/v1/reviews/1/submit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Tapşırıq uğurla yoxlanışa göndərildi"));
    }


    @Test
    void completeReview_Success() throws Exception {
         ReviewRequest request = ReviewRequest.builder()
                .taskId(1L)
                .feedback("Əla işdir!")
                .approved(true)
                .build();

         ReviewResponse response = ReviewResponse.builder()
                .id(100L) // Modelində sahə adı 'id'-dir
                .feedback("Əla işdir!")
                .approved(true)
                .reviewerEmail("manager@company.com")
                .taskId(1L)
                .reviewedAt(LocalDateTime.now())
                .build();

        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("manager@company.com");

        when(reviewService.completeReview(eq(1L), any(ReviewRequest.class), eq("manager@company.com")))
                .thenReturn(response);

         mockMvc.perform(post("/api/v1/reviews/1/complete")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(100L)) // 'reviewId' yox, 'id' olmalıdır
                .andExpect(jsonPath("$.data.reviewerEmail").value("manager@company.com"));
    }

    @Test
    void getReviewByTaskId_Success() throws Exception {
        // 1. Mock Response hazırlayırıq
        ReviewResponse response = ReviewResponse.builder()
                .id(100L)
                .feedback("Test feedback")
                .build();

        // 2. Service metodunu mock edirik (Artıq yalnız taskId qəbul edir)
        when(reviewService.getReviewByTaskId(1L))
                .thenReturn(response);

        // 3. Sorğunu göndəririk (Principal-ı sildik!)
        mockMvc.perform(get("/api/v1/reviews/task/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(100L))
                .andExpect(jsonPath("$.data.feedback").value("Test feedback"));

        // 4. Doğruluğunu yoxlayırıq
        verify(reviewService, times(1)).getReviewByTaskId(1L);
    }
    @Test
    void getAllReviews_Success() throws Exception {
        ReviewResponse r1 = new ReviewResponse();
        ReviewResponse r2 = new ReviewResponse();

        when(reviewService.getAllReviews()).thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/v1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }
}