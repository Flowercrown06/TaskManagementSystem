package az.gultaj.taskmanagement.service;

import az.gultaj.taskmanagement.dto.request.CommentRequest;
import az.gultaj.taskmanagement.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse addComment(CommentRequest request);
    List<CommentResponse> getCommentsByTask(Long taskId);
}
