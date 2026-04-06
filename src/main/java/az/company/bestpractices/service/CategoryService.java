package az.company.bestpractices.service;

import az.company.bestpractices.dto.request.CategoryRequest;
import az.company.bestpractices.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(Long id);
    void deleteCategory(Long id);
}