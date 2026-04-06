package az.company.bestpractices.mapper;

import az.company.bestpractices.dto.request.CommentRequest;
import az.company.bestpractices.dto.response.CommentResponse;
import az.company.bestpractices.entity.Comment;
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