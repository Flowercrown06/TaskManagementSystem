package az.gultaj.taskmanagement.handler;

import az.gultaj.taskmanagement.dto.response.ApiResponse;
import az.gultaj.taskmanagement.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    //   ACCESS DENIED (Yetkisiz giriş cəhdi - 403 Forbidden)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("İcazəsiz giriş cəhdi: {}", ex.getMessage());
        return buildResponse(
                HttpStatus.FORBIDDEN,
                "FORBIDDEN",
                ex.getMessage()
        );
    }

    //  LOGIN ERROR
    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ApiResponse<String>> handleAuthExceptions(RuntimeException ex) {
        log.warn("Authentication xətası: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Email və ya şifrə yanlışdır");
    }

    //   NOT FOUND
    @ExceptionHandler({ResourceNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<ApiResponse<String>> handleNotFoundExceptions(RuntimeException ex) {
        log.error("Resurs tapılmadı: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage());
    }

    //   VALIDATION
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validasiya xətası: {}", errors);

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .status("VALIDATION_ERROR")
                .message("Daxil edilən məlumatlar düzgün deyil")
                .data(errors)
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //   BUSINESS
    @ExceptionHandler({TaskBusinessException.class, CategoryBusinessException.class, IllegalStateException.class})
    public ResponseEntity<ApiResponse<String>> handleBusinessExceptions(RuntimeException ex) {
        log.error("Biznes xətası: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "BUSINESS_ERROR", ex.getMessage());
    }

    //  DUPLICATE
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleAlreadyExists(AlreadyExistsException ex) {
        log.error("Dublikat məlumat: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, "ALREADY_EXISTS", ex.getMessage());
    }

    //  FORBIDDEN
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime xətası: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "RUNTIME_ERROR", ex.getMessage());
    }

    //  DEFAULT (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneralException(Exception ex) {
        log.error("Gözlənilməz xəta: ", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Sistemdə daxili xəta baş verdi");
    }

    //  COMMON RESPONSE
    private ResponseEntity<ApiResponse<String>> buildResponse(HttpStatus status, String statusCode, String message) {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .status(statusCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, status);
    }


}