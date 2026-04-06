package az.company.bestpractices.controller;

import az.company.bestpractices.dto.request.ReviewRequest;
import az.company.bestpractices.dto.response.ApiResponse;
import az.company.bestpractices.dto.response.ReviewResponse;
import az.company.bestpractices.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{taskId}/submit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> submitForReview(@PathVariable Long taskId) {
         reviewService.submitTaskForReview(taskId);

        return ResponseEntity.ok(new ApiResponse<>(
                "SUCCESS",
                "Tapşırıq uğurla yoxlanışa göndərildi",
                null,
                LocalDateTime.now()
        ));
    }

    @PostMapping("/{taskId}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ReviewResponse>> completeReview(
            @PathVariable Long taskId,
            @Valid @RequestBody ReviewRequest request,
            Principal principal) {

        ReviewResponse response = reviewService.completeReview(taskId, request, principal.getName());

        return ResponseEntity.ok(new ApiResponse<>(
                "SUCCESS",
                "Yoxlanış prosesi tamamlandı",
                response,
                LocalDateTime.now()
        ));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReviewByTaskId(@PathVariable Long taskId) {
         var response = reviewService.getReviewByTaskId(taskId);

        return ResponseEntity.ok(ApiResponse.<ReviewResponse>builder()
                .status("SUCCESS")
                .message("Rəy uğurla gətirildi")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build());
    }
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getAllReviews() {
        var reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Bütün rəylər gətirildi", reviews, LocalDateTime.now()));
    }
}