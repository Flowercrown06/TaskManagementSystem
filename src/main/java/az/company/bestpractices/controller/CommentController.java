package az.company.bestpractices.controller;

import az.company.bestpractices.dto.request.CommentRequest;
import az.company.bestpractices.dto.response.ApiResponse;
import az.company.bestpractices.dto.response.CommentResponse;
import az.company.bestpractices.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @Valid @RequestBody CommentRequest request
    ) {
        // Principal.getName() hissəsini artıq Service-in daxilində SecurityUtil həll edir
        CommentResponse data = commentService.addComment(request);

        return ResponseEntity.ok(ApiResponse.<CommentResponse>builder()
                .status("SUCCESS")
                .message("Şərh uğurla əlavə edildi")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build());
    }


    @GetMapping("/task/{taskId}")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(@PathVariable Long taskId) {
        List<CommentResponse> data = commentService.getCommentsByTask(taskId);
        return ResponseEntity.ok(ApiResponse.<List<CommentResponse>>builder()
                .status("SUCCESS")
                .message("Şərhlər siyahısı gətirildi")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build());
    }

}