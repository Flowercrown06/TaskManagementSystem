package az.company.bestpractices.handler;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ErrorResponse {
    private String status;
    private String message;
    private LocalDateTime timestamp;
    private Map<String, String> details;
}