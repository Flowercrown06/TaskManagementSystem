package az.gultaj.taskmanagement.service;

import az.gultaj.taskmanagement.dto.request.CreateTaskRequest;
import az.gultaj.taskmanagement.dto.response.TaskResponse;
import az.gultaj.taskmanagement.enums.TaskStatus;

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