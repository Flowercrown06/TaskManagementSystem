package az.gultaj.taskmanagement.mapper;

import az.gultaj.taskmanagement.dto.response.NotificationResponse;
import az.gultaj.taskmanagement.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper {

     NotificationResponse toResponse(Notification notification);

     List<NotificationResponse> toResponseList(List<Notification> notifications);
}