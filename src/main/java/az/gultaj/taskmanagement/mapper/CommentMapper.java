package az.gultaj.taskmanagement.mapper;

import az.gultaj.taskmanagement.dto.request.CommentRequest;
import az.gultaj.taskmanagement.dto.response.CommentResponse;
import az.gultaj.taskmanagement.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    Comment toEntity(CommentRequest request);


    @Mapping(source = "user.email", target = "userEmail")
    CommentResponse toResponse(Comment comment);

     List<CommentResponse> toResponseList(List<Comment> comments);
}