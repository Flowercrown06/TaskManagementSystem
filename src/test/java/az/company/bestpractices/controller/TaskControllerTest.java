package az.company.bestpractices.controller;

import az.company.bestpractices.dto.request.CreateTaskRequest;
import az.company.bestpractices.dto.response.TaskResponse;
import az.company.bestpractices.enums.TaskPriority;
import az.company.bestpractices.enums.TaskStatus;
import az.company.bestpractices.service.TaskService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class) // Spring-i işə qatmadan Mockito-nu aktiv edirik
class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
         mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    void createTask_Success() throws Exception {
        // 1. Data hazırlanması
        CreateTaskRequest requestDto = new CreateTaskRequest();
        requestDto.setTitle("Yeni Tapşırıq");
        requestDto.setDescription("Test izahı");
        requestDto.setCategoryId(1L); // Bunu əlavə etdik
        requestDto.setPriority(TaskPriority.MEDIUM);

        TaskResponse responseDto = new TaskResponse();
        responseDto.setId(1L);
        responseDto.setTitle("Yeni Tapşırıq");

        // 2. Service-in davranışını təqlid edirik
        when(taskService.createTask(any(CreateTaskRequest.class))).thenReturn(responseDto);

        // 3. HTTP POST sorğusunu simulyasiya edirik
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated()); // İndi 201 olacaq
    }

    @Test
    void createTask_ShouldReturnBadRequest_WhenTitleIsDuplicate() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Mövcud Tapşırıq");
        request.setDescription("Test təsviri"); // Əlavə etdik
        request.setCategoryId(1L);            // Əlavə etdik
        request.setPriority(TaskPriority.MEDIUM); // Əlavə etdik (Enum adını yoxla)

        // Service-ə deyirik ki, bu başlıq artıq var
        when(taskService.isTitleDuplicate("Mövcud Tapşırıq")).thenReturn(true);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR")) // Sənin Controller-də ERROR yazılıb
                .andExpect(jsonPath("$.message").value("Bu başlıqlı tapşırıq artıq mövcuddur"));
    }

    @Test
    void getTaskById_Success() throws Exception {
        TaskResponse response = new TaskResponse();
        response.setId(1L);
        response.setTitle("Test Task");

        when(taskService.getTaskById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void updateStatus_Success() throws Exception {
        TaskResponse response = new TaskResponse();
        response.setId(1L);

        // Status Parametr kimi ötürülür
        when(taskService.updateStatus(eq(1L), any(TaskStatus.class))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/tasks/1/status")
                        .param("status", "IN_PROGRESS")) // RequestParam belə yoxlanılır
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Status yeniləndi"));
    }

    @Test
    void deleteTask_Success() throws Exception {
        // deleteTask void qaytardığı üçün when(...) yazmağa ehtiyac yoxdur

        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Tapşırıq silindi"));
    }
}