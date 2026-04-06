package az.company.bestpractices.controller;

import az.company.bestpractices.dto.request.CategoryRequest;
import az.company.bestpractices.dto.response.ApiResponse;
import az.company.bestpractices.dto.response.CategoryResponse;
import az.company.bestpractices.service.CategoryService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryRequest request) {
         var data = categoryService.createCategory(request);
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Kateqoriya yaradıldı", data, LocalDateTime.now()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll() {
        var data = categoryService.getAllCategories();
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", null, data, LocalDateTime.now()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable Long id) {
        var data = categoryService.getCategoryById(id);
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", null, data, LocalDateTime.now()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Kateqoriya silindi", null, LocalDateTime.now()));
    }
}