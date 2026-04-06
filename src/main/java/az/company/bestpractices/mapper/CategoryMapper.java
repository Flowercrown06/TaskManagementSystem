package az.company.bestpractices.mapper;

import az.company.bestpractices.dto.request.CategoryRequest;
import az.company.bestpractices.dto.response.CategoryResponse;
import az.company.bestpractices.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

     Category toEntity(CategoryRequest request);

     CategoryResponse toResponse(Category category);

     List<CategoryResponse> toResponseList(List<Category> categories);
}