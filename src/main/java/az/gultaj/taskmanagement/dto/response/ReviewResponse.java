package az.gultaj.taskmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor // MapStruct üçün vacibdir
@AllArgsConstructor // Builder üçün vacibdir
public class ReviewResponse {
    private Long id;
    private String feedback;
    private boolean approved;
    private String reviewerEmail;
    private Long taskId;
    private LocalDateTime reviewedAt;
}