package az.company.bestpractices.mapper;

import az.company.bestpractices.dto.response.ReviewResponse;
import az.company.bestpractices.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {

    @Mapping(source = "task.id", target = "taskId")
    @Mapping(source = "reviewer.email", target = "reviewerEmail")
    ReviewResponse toResponse(Review review);

    List<ReviewResponse> toResponseList(List<Review> reviews);
}