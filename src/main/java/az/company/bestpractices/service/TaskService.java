package az.company.bestpractices.service;

import az.company.bestpractices.dto.request.CreateTaskRequest;
import az.company.bestpractices.dto.response.TaskResponse;
import az.company.bestpractices.enums.TaskStatus;

import java.security.Principal;
import java.util.List;

public interface TaskService {
    TaskResponse createTask(CreateTaskRequest request);
    TaskResponse getTaskById(Long id);
    List<TaskResponse> getAllTasksForAdmin();
    List<TaskResponse> getMyTasks();
    TaskResponse updateStatus(Long id, TaskStatus status);
    void deleteTask(Long id);
    boolean isTitleDuplicate(String title);
    void assignTask(Long taskId, Long userId);
}