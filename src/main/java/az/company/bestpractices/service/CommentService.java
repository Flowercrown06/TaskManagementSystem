package az.company.bestpractices.service;

import az.company.bestpractices.dto.request.CommentRequest;
import az.company.bestpractices.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse addComment(CommentRequest request);
    List<CommentResponse> getCommentsByTask(Long taskId);
}
