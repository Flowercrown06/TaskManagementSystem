package az.company.bestpractices.dto.request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @Email(message = "Düzgün email formatı daxil edin")
    @NotBlank(message = "Email boş ola bilməz")
    private String email;

    @NotBlank(message = "Şifrə boş ola bilməz")
    private String password;
}
