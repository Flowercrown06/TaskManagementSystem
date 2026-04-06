package az.company.bestpractices.handler;

import az.company.bestpractices.controller.CategoryController;
import az.company.bestpractices.exception.AlreadyExistsException;
import az.company.bestpractices.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
         mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void handleAlreadyExistsException_ShouldReturn409() throws Exception {
        when(categoryService.createCategory(any()))
                .thenThrow(new AlreadyExistsException("Bu adda kateqoriya artıq mövcuddur"));

        String categoryRequestJson = "{\"name\": \"Java Development\"}";

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryRequestJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("Bu adda kateqoriya artıq mövcuddur"));
    }
}