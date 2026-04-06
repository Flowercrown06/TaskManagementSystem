package az.company.bestpractices.service.impl;

import az.company.bestpractices.dto.request.UpdateUserRoleRequest;
import az.company.bestpractices.dto.response.UserResponse;
import az.company.bestpractices.entity.User;
import az.company.bestpractices.enums.Role;
import az.company.bestpractices.mapper.UserMapper;
import az.company.bestpractices.repository.UserRepository;
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
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("gultaj@gmail.com");
        mockUser.setRole(Role.USER);
        mockUser.setDeleted(false);
    }

    // 1. Rolun uğurla yenilənməsi testi
    @Test
    void updateUserRole_WhenValidRole_ShouldUpdateSuccessfully() {
        // Given
        UpdateUserRoleRequest request = new UpdateUserRoleRequest();
        request.setNewRole("ADMIN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(new UserResponse());

        // When
        userService.updateUserRole(1L, request);

        // Then
        assertEquals(Role.ADMIN, mockUser.getRole());
        verify(userRepository).save(mockUser);
    }

    // 2. Yanlış rol daxil edildikdə (Enum xətası)
    @Test
    void updateUserRole_WhenInvalidRole_ShouldThrowRuntimeException() {
        // Given
        UpdateUserRoleRequest request = new UpdateUserRoleRequest();
        request.setNewRole("INVALID_ROLE");

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUserRole(1L, request);
        });

        assertEquals("Belə bir rol mövcud deyil!", exception.getMessage());
    }

    // 3. Deaktivasiya (Soft Delete) testi
    @Test
    void deactivateUser_ShouldSetDeletedToTrue() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // When
        userService.deactivateUser(1L);

        // Then
        assertTrue(mockUser.isDeleted());
        verify(userRepository).save(mockUser);
    }

    // 4. İstifadəçi tapılmadıqda
    @Test
    void deactivateUser_WhenUserNotFound_ShouldThrowException() {
        // Given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.deactivateUser(99L);
        });
    }
}