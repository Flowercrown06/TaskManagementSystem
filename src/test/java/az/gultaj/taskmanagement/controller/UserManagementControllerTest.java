package az.gultaj.taskmanagement.controller;

import az.gultaj.taskmanagement.dto.request.UpdateUserRoleRequest;
import az.gultaj.taskmanagement.dto.response.UserResponse;
import az.gultaj.taskmanagement.service.UserService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserManagementControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserManagementController userManagementController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
         mockMvc = MockMvcBuilders.standaloneSetup(userManagementController).build();
    }

    @Test
    void changeUserRole_WhenValidRole_ShouldReturnSuccess() throws Exception {
        UpdateUserRoleRequest request = new UpdateUserRoleRequest();
        request.setNewRole("ADMIN");

        UserResponse response = new UserResponse();
        response.setEmail("gultaj@gmail.com");

        when(userService.updateUserRole(eq(1L), any())).thenReturn(response);

        mockMvc.perform(patch("/api/v1/users/1/assign-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("İstifadəçi rolu yeniləndi"));
    }

    @Test
    void changeUserRole_WhenSuperAdmin_ShouldReturnForbidden() {
         UpdateUserRoleRequest request = new UpdateUserRoleRequest();
        request.setNewRole("SUPER_ADMIN");

          org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                mockMvc.perform(patch("/api/v1/users/1/assign-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
        ).hasCauseInstanceOf(SecurityException.class);
    }

    @Test
    void getAllUsers_Success() throws Exception {
        // 1. Data hazırlanması
        UserResponse user1 = new UserResponse();
        user1.setEmail("gultaj@gmail.com");
        UserResponse user2 = new UserResponse();
        user2.setEmail("meryem@gmail.com");

        // Service-in davranışını simulyasiya edirik
        when(userService.findAllActiveUsers()).thenReturn(List.of(user1, user2));

        // 2. HTTP GET sorğusu
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("İstifadəçilər uğurla gətirildi"))
                .andExpect(jsonPath("$.data.length()").value(2)) // Siyahıda 2 nəfər olduğunu yoxlayırıq
                .andExpect(jsonPath("$.data[0].email").value("gultaj@gmail.com"));
    }

    @Test
    void softDeleteUser_Success() throws Exception {
        // 1. HTTP DELETE sorğusu (URL: /api/v1/users/1)
        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("İstifadəçi deaktiv edildi"))
                .andExpect(jsonPath("$.data").isEmpty()); // Data null/empty olmalıdır

        // Opsional: Service-in həqiqətən 1L id-si ilə çağırıldığını yoxlaya bilərik
        verify(userService, times(1)).deactivateUser(1L);
    }
}