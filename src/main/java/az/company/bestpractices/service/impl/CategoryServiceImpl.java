package az.company.bestpractices.service.impl;

import az.company.bestpractices.dto.request.CategoryRequest;
import az.company.bestpractices.dto.response.CategoryResponse;
import az.company.bestpractices.entity.Category;
import az.company.bestpractices.exception.AlreadyExistsException;
import az.company.bestpractices.exception.ResourceNotFoundException;
import az.company.bestpractices.mapper.CategoryMapper;
import az.company.bestpractices.repository.CategoryRepository;
import az.company.bestpractices.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        log.info("Yeni kateqoriya yaradılır: {}", request.getName());

        if (categoryRepository.existsByName(request.getName())) {
            log.error("Kateqoriya artıq mövcuddur: {}", request.getName());
            throw new AlreadyExistsException("Bu kateqoriya artıq mövcuddur: " + request.getName());
        }

        Category category = categoryMapper.toEntity(request);
        Category saved = categoryRepository.save(category);

        return categoryMapper.toResponse(saved);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        log.info("Bütün kateqoriyalar gətirilir");
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toResponseList(categories);
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        log.debug("Kateqoriya axtarılır. ID: {}", id);

        return categoryRepository.findById(id)
                .map(categoryMapper::toResponse)
                .orElseThrow(() -> {
                    log.error("Kateqoriya tapılmadı! ID: {}", id);
                    return new ResourceNotFoundException("Kateqoriya tapılmadı: " + id);
                });
    }

    @Override
    public void deleteCategory(Long id) {
        log.warn("Kateqoriya silinir: ID {}", id);
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Silinəcək kateqoriya tapılmadı: " + id);
        }
        categoryRepository.deleteById(id);
    }
}