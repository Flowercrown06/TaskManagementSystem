package az.gultaj.taskmanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    @NotNull(message = "Task ID mütləqdir")
    private Long taskId;

    @NotBlank(message = "Feedback boş ola bilməz")
    private String feedback;

    @NotNull(message = "Təsdiq statusu qeyd edilməlidir")
    private Boolean approved;
}