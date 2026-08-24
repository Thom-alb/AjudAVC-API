package ajudavcapi.domain.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationDTO(
    @NotBlank String email,
    @NotBlank String password
) {}