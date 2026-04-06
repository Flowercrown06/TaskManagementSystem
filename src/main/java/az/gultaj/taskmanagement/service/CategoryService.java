package az.gultaj.taskmanagement.service;

import az.gultaj.taskmanagement.dto.request.CategoryRequest;
import az.gultaj.taskmanagement.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(Long id);
    void deleteCategory(Long id);
}