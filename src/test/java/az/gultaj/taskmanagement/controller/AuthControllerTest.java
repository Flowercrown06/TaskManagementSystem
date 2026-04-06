package az.gultaj.taskmanagement.controller;

import az.gultaj.taskmanagement.dto.request.LoginRequest;
import az.gultaj.taskmanagement.dto.request.RegisterRequest;
import az.gultaj.taskmanagement.dto.response.LoginResponse;
import az.gultaj.taskmanagement.service.AuthService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void register_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Gultaj Guliyeva");
        request.setEmail("gultajguliyeva2@gmail.com");
        request.setPassword("Gultaj2006");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Qeydiyyat uğurla tamamlandı"));

        verify(authService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void authenticate_Success() throws Exception {
         LoginRequest request = new LoginRequest();
        request.setEmail("gultajguliyeva2@gmail.com");
        request.setPassword("Gultaj2006");

        LoginResponse response = new LoginResponse();
        response.setToken("mock-jwt-token-123");

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

         mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Sistemə giriş uğurludur"))
                .andExpect(jsonPath("$.data.token").value("mock-jwt-token-123"));
    }
}