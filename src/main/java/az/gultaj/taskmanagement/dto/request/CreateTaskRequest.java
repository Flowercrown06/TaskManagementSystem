package az.gultaj.taskmanagement.dto.request;


import az.gultaj.taskmanagement.enums.TaskPriority;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequest {
    @NotBlank(message = "Başlıq boş ola bilməz")
    @Size(min = 3, max = 100, message = "Başlıq 3-100 simvol arası olmalıdır")
    private String title;

    @NotBlank(message = "Təsvir boş ola bilməz")
    private String description;

    @NotNull(message = "Kateqoriya mütləq seçilməlidir")
    private Long categoryId;

    @NotNull(message = "Prioritet qeyd edilməlidir")
    private TaskPriority priority;

    private Long userId; // Tapşırığın kimə veriləcəyini göstərən ID

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Future(message = "Deadline keçmiş tarix ola bilməz")
    private LocalDateTime deadline;
}