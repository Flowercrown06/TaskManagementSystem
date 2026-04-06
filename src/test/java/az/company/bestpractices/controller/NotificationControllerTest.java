package az.company.bestpractices.controller;

import az.company.bestpractices.dto.response.NotificationResponse;
import az.company.bestpractices.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @BeforeEach
    void setUp() {
        // Standalone setup kifayətdir, çünki biz Service-i Mock edirik
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
    }

    @Test
    void getMyNotifications_Success() throws Exception {
        // 1. Mock məlumat hazırlayırıq
        NotificationResponse n1 = new NotificationResponse();
        n1.setMessage("Yeni tapşırıq təyin edildi");

        // 2. Service artıq parametr qəbul etmir!
        // Çünki Service daxildə SecurityUtil istifadə edir.
        when(notificationService.getUserNotifications())
                .thenReturn(List.of(n1));

        // 3. Controller-də artıq .principal(mockPrincipal) ehtiyac yoxdur,
        // çünki Controller-dən Principal arqumentini sildik.
        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].message").value("Yeni tapşırıq təyin edildi"));

        verify(notificationService, times(1)).getUserNotifications();
    }

    @Test
    void markRead_Success() throws Exception {
        // markAsRead metodu üçün dəyişiklik yoxdur, çünki ID hələ də ötürülür
        mockMvc.perform(patch("/api/v1/notifications/1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Bildiriş oxundu"));

        verify(notificationService, times(1)).markAsRead(1L);
    }
}