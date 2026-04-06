package az.company.bestpractices.service;

import az.company.bestpractices.dto.request.ReviewRequest;
import az.company.bestpractices.dto.response.ReviewResponse;
import java.util.List;

public interface ReviewService {
     void submitTaskForReview(Long taskId);

     ReviewResponse completeReview(Long taskId, ReviewRequest request, String reviewerEmail);

    ReviewResponse getReviewByTaskId(Long taskId);
    List<ReviewResponse> getAllReviews();
}

