package az.company.bestpractices.controller;

import az.company.bestpractices.dto.request.UpdateUserRoleRequest;
import az.company.bestpractices.dto.response.ApiResponse;
import az.company.bestpractices.dto.response.UserResponse;
import az.company.bestpractices.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserService userService;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
         List<UserResponse> users = userService.findAllActiveUsers();

        return ResponseEntity.ok(new ApiResponse<>(
                "SUCCESS",
                "İstifadəçilər uğurla gətirildi",
                users,
                LocalDateTime.now()
        ));
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("/{id}/assign-role")
    public ResponseEntity<ApiResponse<UserResponse>> changeUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRoleRequest roleRequest) {

        UserResponse updatedUser = userService.updateUserRole(id, roleRequest);

        return ResponseEntity.ok(new ApiResponse<>(
                "SUCCESS",
                "İstifadəçi rolu yeniləndi",
                updatedUser,
                LocalDateTime.now()
        ));
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> softDeleteUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok(new ApiResponse<>(
                "SUCCESS",
                "İstifadəçi deaktiv edildi",
                null,
                LocalDateTime.now()
        ));
    }
}