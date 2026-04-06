package az.gultaj.taskmanagement.service.impl;

import az.gultaj.taskmanagement.dto.request.CategoryRequest;
import az.gultaj.taskmanagement.dto.response.CategoryResponse;
import az.gultaj.taskmanagement.entity.Category;
import az.gultaj.taskmanagement.exception.ResourceNotFoundException;
import az.gultaj.taskmanagement.mapper.CategoryMapper;
import az.gultaj.taskmanagement.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category mockCategory;
    private CategoryRequest mockRequest;
    private CategoryResponse mockResponse;

    @BeforeEach
    void setUp() {
        mockRequest = new CategoryRequest();
        mockRequest.setName("Backend");

        mockCategory = new Category();
        mockCategory.setId(1L);
        mockCategory.setName("Backend");

        mockResponse = new CategoryResponse();
        mockResponse.setId(1L);
        mockResponse.setName("Backend");
    }

     @Test
    void createCategory_WhenNotExists_ShouldCreateSuccessfully() {
        // Given
        when(categoryRepository.existsByName(anyString())).thenReturn(false);
        when(categoryMapper.toEntity(any(CategoryRequest.class))).thenReturn(mockCategory);
        when(categoryRepository.save(any(Category.class))).thenReturn(mockCategory);
        when(categoryMapper.toResponse(any(Category.class))).thenReturn(mockResponse);

        CategoryResponse result = categoryService.createCategory(mockRequest);

        assertNotNull(result);
        assertEquals("Backend", result.getName());
        verify(categoryRepository, times(1)).save(any());
    }

    @Test
    void createCategory_WhenNameExists_ShouldThrowIllegalStateException() {
         when(categoryRepository.existsByName("Backend")).thenReturn(true);

         assertThrows(IllegalStateException.class, () -> {
            categoryService.createCategory(mockRequest);
        });

        verify(categoryRepository, never()).save(any());
    }

     @Test
    void getCategoryById_WhenExists_ShouldReturnResponse() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
        when(categoryMapper.toResponse(mockCategory)).thenReturn(mockResponse);

        CategoryResponse result = categoryService.getCategoryById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Backend", result.getName());
    }

    @Test
    void getCategoryById_WhenNotExists_ShouldThrowResourceNotFoundException() {
         when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

         assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.getCategoryById(99L);
        });
    }

    @Test
    void deleteCategory_WhenExists_ShouldDelete() {
        when(categoryRepository.existsById(1L)).thenReturn(true);

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).deleteById(1L);
    }
}