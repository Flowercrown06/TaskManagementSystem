package az.company.bestpractices.controller;

import az.company.bestpractices.dto.request.LoginRequest;
import az.company.bestpractices.dto.request.RegisterRequest;
import az.company.bestpractices.dto.response.ApiResponse;
import az.company.bestpractices.dto.response.LoginResponse;
import az.company.bestpractices.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
         authService.register(request);

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .status("SUCCESS")
                .message("Qeydiyyat uğurla tamamlandı")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> authenticate(@Valid @RequestBody LoginRequest request) {
         LoginResponse response = authService.login(request);

        return ResponseEntity.ok(ApiResponse.<LoginResponse>builder()
                .status("SUCCESS")
                .message("Sistemə giriş uğurludur")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build());
    }
}