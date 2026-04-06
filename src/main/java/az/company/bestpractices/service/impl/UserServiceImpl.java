package az.company.bestpractices.service.impl;

import az.company.bestpractices.dto.request.UpdateUserRoleRequest;
import az.company.bestpractices.dto.response.UserResponse;
import az.company.bestpractices.entity.User;
import az.company.bestpractices.enums.Role;
import az.company.bestpractices.exception.UserNotFoundException;
import az.company.bestpractices.mapper.UserMapper;
import az.company.bestpractices.repository.UserRepository;
import az.company.bestpractices.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserResponse> findAllActiveUsers() {
        log.info("Bütün aktiv istifadəçilər bazadan sorğu edilir");
        List<User> users = userRepository.findAllByDeletedFalse();
        return userMapper.toResponseList(users);
    }

    @Override
    public UserResponse updateUserRole(Long id, UpdateUserRoleRequest request) {
        log.info("İstifadəçi rolu yenilənir. ID: {}, İstənilən Rol: {}", id, request.getNewRole());

         if (request.getNewRole().equalsIgnoreCase("SUPER_ADMIN")) {
            log.error("Təhlükəli rol təyini cəhdi bloklandı! ID: {}", id);
            throw new SecurityException("Bu rolun təyin edilməsi qadağandır.");
        }

         User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("İstifadəçi tapılmadı! ID: {}", id);
                    return new UserNotFoundException("İstifadəçi tapılmadı: " + id);
                });

         try {
            Role newRole = Role.valueOf(request.getNewRole().toUpperCase());
            user.setRole(newRole);
            log.info("İstifadəçi rolu dəyişdirildi. ID: {}, Yeni Rol: {}", id, newRole);
        } catch (IllegalArgumentException e) {
            log.warn("Yanlış rol formatı daxil edilib: {}", request.getNewRole());
            throw new RuntimeException("Belə bir rol mövcud deyil!");
        }

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public void deactivateUser(Long id) {
        log.warn("İstifadəçi deaktivasiya (soft delete) edilir. ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Deaktiv ediləcək istifadəçi tapılmadı! ID: {}", id);
                    return new RuntimeException("İstifadəçi tapılmadı: " + id);
                });

        user.setDeleted(true);
        userRepository.save(user);
        log.info("İstifadəçi uğurla deaktiv edildi. ID: {}", id);
    }
}