package az.company.bestpractices.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRoleRequest {
    @NotBlank(message = "Rol qeyd edilməlidir")
    private String newRole;
}