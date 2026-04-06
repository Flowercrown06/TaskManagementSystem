package az.gultaj.taskmanagement.mapper;

import az.gultaj.taskmanagement.dto.request.CategoryRequest;
import az.gultaj.taskmanagement.dto.response.CategoryResponse;
import az.gultaj.taskmanagement.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

     Category toEntity(CategoryRequest request);

     CategoryResponse toResponse(Category category);

     List<CategoryResponse> toResponseList(List<Category> categories);
}