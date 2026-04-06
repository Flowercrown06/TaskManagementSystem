package az.company.bestpractices.mapper;

import az.company.bestpractices.dto.response.NotificationResponse;
import az.company.bestpractices.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper {

     NotificationResponse toResponse(Notification notification);

     List<NotificationResponse> toResponseList(List<Notification> notifications);
}