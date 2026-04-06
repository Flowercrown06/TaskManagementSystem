package az.gultaj.taskmanagement.controller;

import az.gultaj.taskmanagement.dto.response.ApiResponse;
import az.gultaj.taskmanagement.dto.response.NotificationResponse;
import az.gultaj.taskmanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications() {
        // Service artıq daxildən kimin müraciət etdiyini bilir
        var data = notificationService.getUserNotifications();

        return ResponseEntity.ok(ApiResponse.<List<NotificationResponse>>builder()
                .status("SUCCESS")
                .message("Bildirişlər gətirildi")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status("SUCCESS")
                .message("Bildiriş oxundu")
                .timestamp(LocalDateTime.now())
                .build());
    }
}