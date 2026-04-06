package az.gultaj.taskmanagement.service;

import az.gultaj.taskmanagement.dto.request.ReviewRequest;
import az.gultaj.taskmanagement.dto.response.ReviewResponse;
import java.util.List;

public interface ReviewService {
     void submitTaskForReview(Long taskId);

     ReviewResponse completeReview(Long taskId, ReviewRequest request, String reviewerEmail);

    ReviewResponse getReviewByTaskId(Long taskId);
    List<ReviewResponse> getAllReviews();
}

