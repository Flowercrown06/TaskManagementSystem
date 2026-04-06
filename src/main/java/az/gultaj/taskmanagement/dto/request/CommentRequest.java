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
public class CommentRequest {
    @NotBlank(message = "Rəy mətni boş ola bilməz")
    private String content;

    @NotNull(message = "Task ID mütləqdir")
    private Long taskId;
}