package az.gultaj.taskmanagement.controller;

import az.gultaj.taskmanagement.dto.request.CreateTaskRequest;
import az.gultaj.taskmanagement.dto.response.ApiResponse;
import az.gultaj.taskmanagement.dto.response.TaskResponse;
import az.gultaj.taskmanagement.enums.TaskStatus;
import az.gultaj.taskmanagement.service.TaskService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(@Valid @RequestBody CreateTaskRequest request) {
        TaskResponse response = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<TaskResponse>builder()
                        .status("SUCCESS")
                        .message("Tapşırıq uğurla yaradıldı")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // 1. ADMIN üçün: Bütün sistemdəki tasklar
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getAllTasksForAdmin() {
        List<TaskResponse> tasks = taskService.getAllTasksForAdmin();
        return ResponseEntity.ok(ApiResponse.<List<TaskResponse>>builder()
                .status("SUCCESS")
                .data(tasks)
                .timestamp(LocalDateTime.now())
                .build());
    }


    // 2. USER/ADMIN üçün: Ancaq login olanın öz taskları
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getMyTasks() {
        List<TaskResponse> tasks = taskService.getMyTasks();
        return ResponseEntity.ok(ApiResponse.<List<TaskResponse>>builder()
                .status("SUCCESS")
                .data(tasks)
                .timestamp(LocalDateTime.now())
                .build());
    }



    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(@PathVariable Long id) {
        // Service artıq daxili SecurityUtil vasitəsilə daxil olan istifadəçini və rollarını bilir
        TaskResponse response = taskService.getTaskById(id);

        return ResponseEntity.ok(ApiResponse.<TaskResponse>builder()
                .status("SUCCESS")
                .message("Tapşırıq uğurla gətirildi")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build());
    }

    // 4. Statusu yeniləmək: ADMIN və SUPER_ADMIN (Təsdiqləmə prosesi üçün)
    // Əgər USER-in də bitirdiyi işi "Done" etməsini istəyirsənsə, 'USER' də əlavə edə bilərsən.
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TaskResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam TaskStatus status) {

        TaskResponse response = taskService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.<TaskResponse>builder()
                .status("SUCCESS")
                .message("Status yeniləndi")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build());
    }
    @PatchMapping("/{taskId}/assign/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignTask(@PathVariable Long taskId, @PathVariable Long userId) {
        taskService.assignTask(taskId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status("SUCCESS")
                .message("Tapşırıq silindi")
                .timestamp(LocalDateTime.now())
                .build());
    }
}