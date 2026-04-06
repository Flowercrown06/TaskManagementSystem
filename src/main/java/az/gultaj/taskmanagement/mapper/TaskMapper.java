package az.gultaj.taskmanagement.mapper;

import az.gultaj.taskmanagement.dto.request.CreateTaskRequest;
import az.gultaj.taskmanagement.dto.response.TaskResponse;
import az.gultaj.taskmanagement.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    Task toEntity(CreateTaskRequest request);

    @Mapping(source = "user.email", target = "assignedUserEmail")
    @Mapping(source = "category.name", target = "categoryName")
    TaskResponse toResponse(Task task);

    java.util.List<TaskResponse> toResponseList(java.util.List<Task> tasks);
}