package az.gultaj.taskmanagement.controller;

import az.gultaj.taskmanagement.dto.request.CategoryRequest;
import az.gultaj.taskmanagement.dto.response.CategoryResponse;
import az.gultaj.taskmanagement.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
    }

    @Test
    void create_Success() throws Exception {
         CategoryRequest request = new CategoryRequest();
        request.setName("Backend"); // Əgər @NotBlank varsa, mütləq doldurmalısan

        CategoryResponse response = new CategoryResponse();
        response.setId(1L);
        response.setName("Backend");

        when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(response);


        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Kateqoriya yaradıldı"))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void getAll_Success() throws Exception {
        CategoryResponse c1 = new CategoryResponse();
        c1.setName("Development");

        when(categoryService.getAllCategories()).thenReturn(List.of(c1));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Development"));
    }

    @Test
    void getById_Success() throws Exception {
        CategoryResponse response = new CategoryResponse();
        response.setId(1L);
        response.setName("Cloud");

        when(categoryService.getCategoryById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("Cloud"));
    }

    @Test
    void delete_Success() throws Exception {
         mockMvc.perform(delete("/api/v1/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Kateqoriya silindi"));

         verify(categoryService, times(1)).deleteCategory(1L);
    }
}