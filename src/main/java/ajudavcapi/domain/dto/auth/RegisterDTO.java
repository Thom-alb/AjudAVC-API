package ajudavcapi.domain.dto.auth;

import ajudavcapi.domain.enums.GroupRole;
import jakarta.validation.constraints.NotBlank;

public record RegisterDTO(
    @NotBlank String login,
    @NotBlank String password,
    @NotBlank GroupRole role
) {
    
}
