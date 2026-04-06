package az.company.bestpractices.service;

import az.company.bestpractices.dto.request.LoginRequest;
import az.company.bestpractices.dto.request.RegisterRequest;
import az.company.bestpractices.dto.response.LoginResponse;
import az.company.bestpractices.entity.User;
import az.company.bestpractices.enums.Role;
import az.company.bestpractices.exception.AlreadyExistsException;
import az.company.bestpractices.mapper.UserMapper;
import az.company.bestpractices.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    @Transactional // Mütləq əlavə et!
    public void register(RegisterRequest request) {
        log.info("Yeni qeydiyyat cəhdi. Email: {}", request.getEmail());

        userRepository.findByEmail(request.getEmail())
                .ifPresent(u -> {
                    log.warn("Qeydiyyat uğursuz: Email artıq mövcuddur - {}", request.getEmail());
                    throw new AlreadyExistsException("Bu email (" + request.getEmail() + ") artıq qeydiyyatdan keçib!");
                });

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .deleted(false)
                .build();

        // 1. Öncə istifadəçini yadda saxlayırıq (ID burada yaranır)
        User savedUser = userRepository.save(user);

        // 2. Artıq savedUser.getId() tam təhlükəsizdir
        notificationService.sendNotification(savedUser.getId(),
                "Sistemimizə xoş gəldiniz! Admin tərəfindən task təyin olunmasını gözləyin.");

        log.info("İstifadəçi uğurla qeydiyyatdan keçdi: {}", request.getEmail());
    }

    public LoginResponse login(LoginRequest request) {
        log.info("Giriş cəhdi başlandı. Email: {}", request.getEmail());

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("İstifadəçi tapılmadı"));

        String token = jwtService.generateToken(user);

        log.info("Giriş uğurlu. Token yaradıldı: {}", request.getEmail());

        return userMapper.toLoginResponse(user, token);
    }
}